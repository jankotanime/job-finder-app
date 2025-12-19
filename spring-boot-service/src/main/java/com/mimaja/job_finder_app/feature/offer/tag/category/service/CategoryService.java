package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import java.util.UUID;

public interface CategoryService {
    Category getCategoryById(UUID id);

    CategoryResponseDto createCategory(CategoryCreateRequestDto categoryCreateRequestDto);
}
