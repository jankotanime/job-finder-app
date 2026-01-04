package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceUser {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public Page<CategoryResponseDto> getAllCategories(
            CategoryFilterRequestDto categoryFilterRequestDto, Pageable pageable) {
        return categoryService
                .getAllCategories(categoryFilterRequestDto, pageable)
                .map(categoryMapper::toResponseDto);
    }

    public CategoryResponseDto getCategoryById(UUID categoryId) {
        return categoryMapper.toResponseDto(categoryService.getCategoryById(categoryId));
    }
}
