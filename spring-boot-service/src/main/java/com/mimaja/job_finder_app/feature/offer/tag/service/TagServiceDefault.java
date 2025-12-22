package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.repository.TagRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceDefault implements TagService {
    private final TagRepository tagRepository;
    private final CategoryService categoryService;
    private final TagMapper tagMapper;

    @Override
    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Page<Tag> getAllByCategoryId(UUID categoryId, Pageable pageable) {
        return tagRepository.getAllByCategoryId(categoryId, pageable);
    }

    @Override
    public Tag getTagById(UUID tagId) {
        return getOrThrow(tagId);
    }

    @Override
    @Transactional
    public Tag createTag(TagCreateRequestDto dto) {
        throwErrorIfTagExists(dto.name());
        Category category = categoryService.getCategoryById(dto.categoryId());
        Tag tag = tagMapper.toEntity(dto);
        tag.setCategory(category);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(UUID tagId, TagCreateRequestDto dto) {
        Tag tag = getOrThrow(tagId);
        if (!dto.name().equals(tag.getName())) {
            throwErrorIfTagExists(dto.name());
        }
        Category category = categoryService.getCategoryById(dto.categoryId());
        tag.update(dto, category);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(UUID tagId) {
        Tag tag = getOrThrow(tagId);
        tagRepository.delete(tag);
    }

    private void throwErrorIfTagExists(String name) {
        Optional<Tag> tagOpt = tagRepository.findByName(name);
        if (tagOpt.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.TAG_ALREADY_EXISTS);
        }
    }

    private Tag getOrThrow(UUID tagId) {
        return tagRepository
                .findById(tagId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.TAG_NOT_FOUND));
    }
}
