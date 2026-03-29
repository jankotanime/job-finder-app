package com.mimaja.job_finder_app.feature.user.filterspecification;

import com.mimaja.job_finder_app.feature.user.dto.UserFilterRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.model.UserRole;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserFilterSpecification {
    private UserFilterSpecification() {}

    public static Specification<User> filter(UserFilterRequestDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            query.distinct(true);

            if (dto.username() != null && !dto.username().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + dto.username() + "%"));
            }

            if (dto.email() != null && !dto.email().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + dto.email() + "%"));
            }

            if (dto.firstDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dto.firstDate()));
            }

            if (dto.lastDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dto.lastDate()));
            }

            predicates.add(cb.equal(root.get("role"), UserRole.USER));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
