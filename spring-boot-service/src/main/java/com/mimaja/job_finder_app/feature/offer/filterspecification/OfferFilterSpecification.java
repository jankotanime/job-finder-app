package com.mimaja.job_finder_app.feature.offer.filterspecification;

import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class OfferFilterSpecification {
    private OfferFilterSpecification() {}

    public static Specification<Offer> filter(OfferFilterRequestDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            query.distinct(true);

            if (dto.firstDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateAndTime"), dto.firstDate()));
            }

            if (dto.lastDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateAndTime"), dto.lastDate()));
            }

            if (dto.minSalary() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), dto.minSalary()));
            }

            if (dto.maxSalary() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salary"), dto.maxSalary()));
            }

            if (dto.tags() != null && !dto.tags().isEmpty()) {
                Join<Offer, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("id").in(dto.tags()));
            }

            if (dto.categories() != null && !dto.categories().isEmpty()) {
                Join<Offer, Tag> tagJoin = root.join("tags", JoinType.INNER);
                Join<Tag, Category> categoryJoin = tagJoin.join("category", JoinType.INNER);

                predicates.add(categoryJoin.get("id").in(dto.categories()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
