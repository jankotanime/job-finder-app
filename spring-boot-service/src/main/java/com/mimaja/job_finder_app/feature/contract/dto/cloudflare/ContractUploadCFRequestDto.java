package com.mimaja.job_finder_app.feature.contract.dto.cloudflare;

import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContractUploadCFRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey,
        @NotNull Offer offer) {
    public static ContractUploadCFRequestDto from(ProcessedFileDetails details, Offer offer) {
        return new ContractUploadCFRequestDto(
                details.fileName(),
                details.mimeType(),
                details.fileSize(),
                details.storageKey(),
                offer);
    }
}
