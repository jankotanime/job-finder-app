package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.repository.CvRepository;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class CvServiceDefault implements CvService {
    private final CvRepository cvRepository;
    private final CvMapper cvMapper;
    private final UserService userService;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    // Example upload
    public String uploadFile(MultipartFile file) {
        // 1. Resolve filename and content type
        String original =
                Optional.ofNullable(file.getOriginalFilename())
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                ApplicationExceptionReason.FILE_NAME_MISSING))
                        .toLowerCase();
        String contentType =
                Optional.ofNullable(file.getContentType())
                        .orElseThrow(
                                () ->
                                        new ApplicationException(
                                                ApplicationExceptionReason.CONTENT_TYPE_UNKNOWN));

        // 2. Decide folder by extension
        String ext = getFileExtension(original);
        String folder =
                switch (ext) {
                    case "jpg", "jpeg", "png", "gif" -> "images";
                    case "mp4", "mov" -> "videos";
                    case "pdf", "doc", "docx", "txt" -> "documents";
                    default ->
                            throw new ApplicationException(
                                    ApplicationExceptionReason.UNSUPPORTED_FILE_TYPE);
                };

        // 3. Build object key
        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), original);

        // 4. Prepare S3 Put request
        PutObjectRequest req =
                PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build();

        // 5. Perform upload
        try {
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION);
        }

        return key;
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return filename.substring(idx + 1);
    }

    @Override
    public Cv uploadCv(CvUploadRequestDto dto, UUID userId) {
        User user = userService.getUserById(userId);
        Cv cv = cvMapper.toEntity(dto);
        cv.setUser(user);
        cv.setStorageKey(UUID.randomUUID().toString()); // generated key from Cloudflare R2 Storage
        return cvRepository.save(cv);
    }

    @Override
    public Cv getCvById(UUID cvId) {
        return new Cv();
    }

    @Override
    public List<Cv> getCvsByUserId(UUID userId) {
        return List.of(new Cv(), new Cv());
    }

    @Override
    public Cv updateCv(CvUploadRequestDto dto, UUID cvId, UUID userId) {
        return new Cv();
    }

    @Override
    public void deleteCv(UUID cvId) {
        Cv cv = getOrThrow(cvId);
        cvRepository.delete(cv);
    }

    @Override
    public void deleteAllCvsForUser(UUID userId) {}

    private Cv getOrThrow(UUID cvId) {
        return cvRepository
                .findById(cvId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.CV_NOT_FOUND));
    }
}
