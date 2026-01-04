package com.mimaja.job_finder_app.shared.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.interfaces.FileSource;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public ResponseInputStream<GetObjectResponse> getFile(String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
        return s3Client.getObject(getRequest);
    }

    public void deleteFile(String key) {
        DeleteObjectRequest delRequest =
                DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        s3Client.deleteObject(delRequest);
    }

    public void uploadFile(ProcessedFileDetails fileDetails) {
        PutObjectRequest putRequest =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileDetails.storageKey())
                        .contentType(fileDetails.contentType())
                        .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(fileDetails.bytes()));
    }

    public ProcessedFileDetails processFileDetails(FileSource fileSource, String folder) {
        String fileName = fileSource.getOriginalFilename();
        String contentType = fileSource.getContentType();
        MimeType ext = getFileExtension(fileName);

        throwErrorIfWrongDocumentFormat(folder, ext);
        throwErrorIfWrongPhotoFormat(folder, ext);

        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), fileName);
        return new ProcessedFileDetails(
                fileName, contentType, ext, key, fileSource.getSize(), fileSource.getBytes());
    }

    private void throwErrorIfWrongPhotoFormat(String folder, MimeType ext) {
        if (folder.contains(FileFolderName.PHOTOS.getFolderName())
                && !ext.equals(MimeType.JPG)
                && !ext.equals(MimeType.JPEG)
                && !ext.equals(MimeType.PNG)) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PHOTO_FORMAT);
        }
    }

    private void throwErrorIfWrongDocumentFormat(String folder, MimeType ext) {
        if (folder.contains(FileFolderName.DOCUMENTS.getFolderName())
                && !ext.equals(MimeType.DOCX)
                && !ext.equals(MimeType.PDF)) {
            throw new BusinessException(BusinessExceptionReason.WRONG_DOCUMENT_FILE_FORMAT);
        }
    }

    private MimeType getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return MimeType.valueOf(filename.substring(idx + 1).toUpperCase());
    }
}
