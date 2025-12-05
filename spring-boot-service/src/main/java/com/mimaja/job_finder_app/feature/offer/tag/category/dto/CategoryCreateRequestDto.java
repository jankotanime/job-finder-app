package com.mimaja.job_finder_app.feature.offer.tag.category.dto;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryCreateRequestDto(@NotBlank String name, @NotNull CategoryColor color) {}
