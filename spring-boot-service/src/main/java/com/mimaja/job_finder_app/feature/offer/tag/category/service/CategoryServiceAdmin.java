package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceAdmin {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryCreateRequestDto categoryCreateRequestDto) {
        return categoryMapper.toResponseDto(
                categoryService.createCategory(categoryCreateRequestDto));
    }

    public CategoryResponseDto updateCategory(
            UUID categoryId, CategoryCreateRequestDto categoryUpdateRequestDto) {
        return categoryMapper.toResponseDto(
                categoryService.updateCategory(categoryId, categoryUpdateRequestDto));
    }

    public void deleteCategory(UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
