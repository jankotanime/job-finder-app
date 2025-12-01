package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.repository.TagRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceDefault implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Tag getTagById(UUID tagId) {
        return getOrThrow(tagId);
    }

    private Tag getOrThrow(UUID tagId) {
        return tagRepository
                .findById(tagId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.TAG_NOT_FOUND));
    }
}
