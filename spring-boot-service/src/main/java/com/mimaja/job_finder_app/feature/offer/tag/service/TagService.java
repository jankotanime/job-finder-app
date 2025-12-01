package com.mimaja.job_finder_app.feature.offer.tag.service;

import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import java.util.UUID;

public interface TagService {
    Tag getTagById(UUID tagId);
}
