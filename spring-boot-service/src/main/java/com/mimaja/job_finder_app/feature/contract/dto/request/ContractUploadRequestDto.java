package com.mimaja.job_finder_app.feature.contract.dto.request;

import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContractUploadRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey,
        @NotNull Job job) {
    public static ContractUploadRequestDto from(ProcessedFileDetails details, Job job) {
        return new ContractUploadRequestDto(
                details.fileName(),
                details.mimeType(),
                details.fileSize(),
                details.storageKey(),
                job);
    }
}
