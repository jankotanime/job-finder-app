package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.repository.CategoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceDefault implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category getCategoryById(UUID id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateRequestDto dto) {
        Optional<Category> categoryOpt = categoryRepository.findByName(dto.name());
        if (categoryOpt.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.CATEGORY_ALREADY_EXISTS);
        }
        Category category = categoryMapper.toEntity(dto);
        category = categoryRepository.save(category);
        return categoryMapper.toResponseDto(category);
    }

    private Category getOrThrow(UUID id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(BusinessExceptionReason.CATEGORY_NOT_FOUND));
    }
}
