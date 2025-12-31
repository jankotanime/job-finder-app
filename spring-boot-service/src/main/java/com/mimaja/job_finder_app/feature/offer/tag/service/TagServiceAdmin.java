package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceAdmin {
    private final TagService tagService;
    private final TagMapper tagMapper;

    public Page<TagResponseDto> getAllTags(Pageable pageable) {
        return tagService.getAllTags(pageable).map(tagMapper::toResponseDto);
    }

    public TagResponseDto createTag(TagCreateRequestDto tagCreateRequestDto) {
        return tagMapper.toResponseDto(tagService.createTag(tagCreateRequestDto));
    }

    public TagResponseDto updateTag(UUID tagId, TagCreateRequestDto tagUpdateRequestDto) {
        return tagMapper.toResponseDto(tagService.updateTag(tagId, tagUpdateRequestDto));
    }

    public void deleteTag(UUID tagId) {
        tagService.deleteTag(tagId);
    }
}
