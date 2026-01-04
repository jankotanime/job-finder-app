package com.mimaja.job_finder_app.feature.offer.tag.category.repository;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository
        extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByName(String name);
}
