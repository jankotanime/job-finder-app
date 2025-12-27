package com.mimaja.job_finder_app.feature.cv.dto;

import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CvUpdateRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey) {
    public static CvUpdateRequestDto from(ProcessedFileDetails details) {
        return new CvUpdateRequestDto(
                details.fileName(), details.mimeType(), details.fileSize(), details.storageKey());
    }
}
