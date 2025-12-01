package com.mimaja.job_finder_app.feature.offer.tag.dto;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import java.util.UUID;

public record TagResponseDto(
        UUID id, String name, String categoryName, CategoryColor categoryColor) {}
