package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<Category> getAllCategories(Pageable pageable);

    Category getCategoryById(UUID id);

    Category createCategory(CategoryCreateRequestDto categoryCreateRequestDto);

    Category updateCategory(UUID id, CategoryCreateRequestDto categoryCreateRequestDto);

    void deleteCategory(UUID id);
}
