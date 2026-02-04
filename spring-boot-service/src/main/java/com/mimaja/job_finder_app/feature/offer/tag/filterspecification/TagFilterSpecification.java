package com.mimaja.job_finder_app.feature.offer.tag.filterspecification;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class TagFilterSpecification {
    private TagFilterSpecification() {}

    public static Specification<Tag> filter(TagFilterRequestDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            query.distinct(true);

            if (dto.name() != null && !dto.name().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")), "%" + dto.name().toLowerCase() + "%"));
            }

            if (dto.categories() != null) {
                predicates.add(
                        root.join("category", JoinType.INNER)
                                .get("name")
                                .in((Object[]) dto.categories()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
