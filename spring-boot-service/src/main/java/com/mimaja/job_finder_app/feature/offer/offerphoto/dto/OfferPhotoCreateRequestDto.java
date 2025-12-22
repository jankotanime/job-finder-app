package com.mimaja.job_finder_app.feature.offer.offerphoto.dto;

import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;

public record OfferPhotoCreateRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull BigInteger fileSize,
        @NotBlank String storageKey) {}
