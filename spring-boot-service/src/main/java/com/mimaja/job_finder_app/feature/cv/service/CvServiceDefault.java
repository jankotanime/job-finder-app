package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUpdateRequestDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.model.MimeType;
import com.mimaja.job_finder_app.feature.cv.repository.CvRepository;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessedFileDetails {
        String fileName;
        String contentType;
        MimeType mimeType;
        String storageKey;
    }

    @Override
    public CvResponseDto uploadCv(MultipartFile file, UUID userId) {
        ProcessedFileDetails fileDetails = processFileDetails(file);

        PutObjectRequest req =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileDetails.storageKey)
                        .contentType(fileDetails.contentType)
                        .build();

        try {
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION);
        }

        User user = userService.getUserById(userId);
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        CvUploadRequestDto dto =
                new CvUploadRequestDto(
                        fileDetails.fileName,
                        fileDetails.mimeType,
                        fileSize,
                        fileDetails.storageKey,
                        user);
        Cv cv = Cv.from(dto);
        return cvMapper.toResponseDto(cvRepository.save(cv));
    }

    @Override
    public CvResponseDto getCvById(UUID cvId) {
        return cvMapper.toResponseDto(getOrThrow(cvId));
    }

    @Override
    public List<CvResponseDto> getCvsByUserId(UUID userId) {
        return cvRepository.findAllCvsByUserId(userId).stream()
                .map(cvMapper::toResponseDto)
                .toList();
    }

    @Override
    public CvResponseDto updateCv(MultipartFile file, UUID cvId) {
        Cv cv = getOrThrow(cvId);
        ProcessedFileDetails fileDetails = processFileDetails(file);

        DeleteObjectRequest reqDel =
                DeleteObjectRequest.builder().bucket(bucket).key(cv.getStorageKey()).build();

        PutObjectRequest req =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileDetails.storageKey)
                        .contentType(fileDetails.contentType)
                        .build();

        try {
            s3Client.deleteObject(reqDel);
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_REPLACE_EXCEPTION);
        }

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        CvUpdateRequestDto dto =
                new CvUpdateRequestDto(
                        fileDetails.fileName,
                        fileDetails.mimeType,
                        fileSize,
                        fileDetails.storageKey);
        cv.update(dto);
        return cvMapper.toResponseDto(cvRepository.save(cv));
    }

    @Override
    public void deleteCv(UUID cvId) {
        Cv cv = getOrThrow(cvId);
        s3Client.deleteObject(
                DeleteObjectRequest.builder().bucket(bucket).key(cv.getStorageKey()).build());
        cvRepository.delete(cv);
    }

    @Override
    public void deleteAllCvsForUser(UUID userId) {
        List<Cv> cvList = cvRepository.findAllCvsByUserId(userId);
        cvList.forEach(
                cv ->
                        s3Client.deleteObject(
                                DeleteObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(cv.getStorageKey())
                                        .build()));
        cvRepository.deleteAll(cvList);
    }

    private Cv getOrThrow(UUID cvId) {
        return cvRepository
                .findById(cvId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.CV_NOT_FOUND));
    }

    private MimeType getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return MimeType.valueOf(filename.substring(idx + 1).toUpperCase());
    }

    private String getFileName(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.FILE_NAME_MISSING))
                .toLowerCase();
    }

    private String getContentType(MultipartFile file) {
        return Optional.ofNullable(file.getContentType())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.CONTENT_TYPE_UNKNOWN));
    }

    private ProcessedFileDetails processFileDetails(MultipartFile file) {
        String fileName = getFileName(file);
        String contentType = getContentType(file);
        MimeType ext = getFileExtension(fileName);
        if (!ext.equals(MimeType.DOCX) && !ext.equals(MimeType.PDF)) {
            throw new BusinessException(BusinessExceptionReason.WRONG_CV_FILE_FORMAT);
        }
        String folder = "documents";

        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), fileName);
        return new ProcessedFileDetails(fileName, contentType, ext, key);
    }
}
