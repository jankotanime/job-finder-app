package com.mimaja.job_finder_app.feature.cv.dto;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CvUploadRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey,
        @NotNull User user) {
    public static CvUploadRequestDto from(ProcessedFileDetails details, User user) {
        return new CvUploadRequestDto(
                details.fileName(),
                details.mimeType(),
                details.fileSize(),
                details.storageKey(),
                user);
    }
}
