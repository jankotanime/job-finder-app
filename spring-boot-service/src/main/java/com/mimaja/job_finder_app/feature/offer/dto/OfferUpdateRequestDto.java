package com.mimaja.job_finder_app.feature.offer.dto;

import com.mimaja.job_finder_app.feature.offer.location.dto.LocationCreateRequestDto;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record OfferUpdateRequestDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Double salary,
        @Valid @NotNull LocationCreateRequestDto location,
        @NotNull int maxParticipants,
        @Valid @NotNull Set<UUID> tags,
        @Nullable @Valid MultipartFile offerPhoto) {}
