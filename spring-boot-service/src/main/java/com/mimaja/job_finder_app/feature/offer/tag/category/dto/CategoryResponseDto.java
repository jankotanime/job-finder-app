package com.mimaja.job_finder_app.feature.offer.tag.category.dto;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import java.util.UUID;

public record CategoryResponseDto(UUID id, String name, CategoryColor color) {}
