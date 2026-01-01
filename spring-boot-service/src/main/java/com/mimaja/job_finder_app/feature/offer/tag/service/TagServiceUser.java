package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceUser {
    private final TagService tagService;
    private final TagMapper tagMapper;

    public Page<TagResponseDto> getAllTags(Pageable pageable) {
        return tagService.getAllTags(pageable).map(tagMapper::toResponseDto);
    }

    public Page<TagResponseDto> getAllByCategoryId(UUID categoryId, Pageable pageable) {
        return tagService.getAllByCategoryId(categoryId, pageable).map(tagMapper::toResponseDto);
    }

    public TagResponseDto getTagById(UUID tagId) {
        return tagMapper.toResponseDto(tagService.getTagById(tagId));
    }
}
