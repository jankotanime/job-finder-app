package com.mimaja.job_finder_app.shared.dto;

import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProcessedFileDetails(
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotNull MimeType mimeType,
        @NotBlank String storageKey,
        @NotNull long fileSize,
        @NotNull byte[] bytes) {}
