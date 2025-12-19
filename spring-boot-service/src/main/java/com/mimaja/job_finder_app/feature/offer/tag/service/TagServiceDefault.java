package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.repository.TagRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceDefault implements TagService {
    private final TagRepository tagRepository;
    private final CategoryService categoryService;
    private final TagMapper tagMapper;

    @Override
    public Tag getTagById(UUID tagId) {
        return getOrThrow(tagId);
    }

    @Override
    @Transactional
    public TagResponseDto createTag(TagCreateRequestDto dto) {
        Optional<Tag> tagOpt = tagRepository.findByName(dto.name());
        if (tagOpt.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.TAG_ALREADY_EXISTS);
        }
        Category category = categoryService.getCategoryById(dto.categoryId());
        Tag tag = tagMapper.toEntity(dto);
        tag.setCategory(category);
        tagRepository.save(tag);
        return tagMapper.toResponseDto(tag);
    }

    private Tag getOrThrow(UUID tagId) {
        return tagRepository
                .findById(tagId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.TAG_NOT_FOUND));
    }
}
