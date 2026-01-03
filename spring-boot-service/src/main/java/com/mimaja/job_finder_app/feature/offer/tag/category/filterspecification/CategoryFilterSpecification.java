package com.mimaja.job_finder_app.feature.offer.tag.category.filterspecification;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class CategoryFilterSpecification {
    private CategoryFilterSpecification() {}

    public static Specification<Category> filter(CategoryFilterRequestDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            query.distinct(true);

            if (dto.name() != null && !dto.name().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")), "%" + dto.name().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
