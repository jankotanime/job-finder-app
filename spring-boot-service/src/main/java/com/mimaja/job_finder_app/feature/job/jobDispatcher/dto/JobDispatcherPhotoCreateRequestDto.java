package com.mimaja.job_finder_app.feature.job.jobDispatcher.dto;

import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobDispatcherPhotoCreateRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey) {
    public static JobDispatcherPhotoCreateRequestDto from(ProcessedFileDetails details) {
        return new JobDispatcherPhotoCreateRequestDto(
                details.fileName(), details.mimeType(), details.fileSize(), details.storageKey());
    }
}
