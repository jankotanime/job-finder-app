package com.mimaja.job_finder_app.feature.offer.tag.category.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.repository.CategoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceDefault implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional
    public Category createCategory(CategoryCreateRequestDto dto) {
        throwErrorIfCategoryExists(dto.name());
        Category category = categoryMapper.toEntity(dto);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, CategoryCreateRequestDto dto) {
        Category category = getOrThrow(id);
        if (!dto.name().equals(category.getName())) {
            throwErrorIfCategoryExists(dto.name());
        }
        category.update(dto);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getOrThrow(id);
        categoryRepository.delete(category);
    }

    private void throwErrorIfCategoryExists(String name) {
        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        if (categoryOpt.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.CATEGORY_ALREADY_EXISTS);
        }
    }

    private Category getOrThrow(UUID id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(BusinessExceptionReason.CATEGORY_NOT_FOUND));
    }
}
