package com.mimaja.job_finder_app.feature.offer.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TagCreateRequestDto(@NotBlank String name, @NotNull UUID categoryId) {}
