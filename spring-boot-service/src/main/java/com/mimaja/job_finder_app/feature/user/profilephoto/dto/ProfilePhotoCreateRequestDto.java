package com.mimaja.job_finder_app.feature.user.profilephoto.dto;

import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;

public record ProfilePhotoCreateRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull BigInteger fileSize,
        @NotBlank String storageKey) {}
