package com.mimaja.job_finder_app.feature.offer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OfferUpdateRequestDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Future LocalDateTime dateAndTime,
        @NotNull Double salary,
        @NotNull int maxParticipants,
        @Valid @NotNull Set<UUID> tags) {}
