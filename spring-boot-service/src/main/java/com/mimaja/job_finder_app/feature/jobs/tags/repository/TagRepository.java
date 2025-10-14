package com.mimaja.job_finder_app.feature.jobs.tags.repository;

import com.mimaja.job_finder_app.feature.jobs.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
