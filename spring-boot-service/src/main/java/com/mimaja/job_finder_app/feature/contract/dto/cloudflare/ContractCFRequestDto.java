package com.mimaja.job_finder_app.feature.contract.dto.cloudflare;

import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContractCFRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey) {
    public static ContractCFRequestDto from(ProcessedFileDetails details) {
        return new ContractCFRequestDto(
                details.fileName(), details.mimeType(), details.fileSize(), details.storageKey());
    }
}
