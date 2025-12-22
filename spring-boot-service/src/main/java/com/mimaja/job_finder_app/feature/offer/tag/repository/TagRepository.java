package com.mimaja.job_finder_app.feature.offer.tag.repository;

import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t WHERE t.category.id = :categoryId")
    Page<Tag> getAllByCategoryId(UUID categoryId, Pageable pageable);
}
