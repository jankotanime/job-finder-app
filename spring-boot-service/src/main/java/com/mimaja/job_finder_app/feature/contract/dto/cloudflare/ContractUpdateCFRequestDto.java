package com.mimaja.job_finder_app.feature.contract.dto.cloudflare;

import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContractUpdateCFRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey,
        @NotNull UUID contractId) {
    public static ContractUpdateCFRequestDto from(
            ContractUpdateCFRequestDto details, UUID contractId) {
        return new ContractUpdateCFRequestDto(
                details.fileName(),
                details.mimeType(),
                details.fileSize(),
                details.storageKey(),
                contractId);
    }
}
