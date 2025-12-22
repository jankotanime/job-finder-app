package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {
    Page<Tag> getAllTags(Pageable pageable);

    Page<Tag> getAllByCategoryId(UUID categoryId, Pageable pageable);

    Tag getTagById(UUID tagId);

    Tag createTag(TagCreateRequestDto tagCreateRequestDto);

    Tag updateTag(UUID tagId, TagCreateRequestDto tagUpdateRequestDto);

    void deleteTag(UUID tagId);
}
