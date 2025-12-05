package com.mimaja.job_finder_app.feature.offer.tag.category.repository;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
}
