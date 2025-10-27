package com.mimaja.job_finder_app.feature.jobs.tags.repository;import com.mimaja.job_finder_app.feature.jobs.tags.model.Tag;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {}
