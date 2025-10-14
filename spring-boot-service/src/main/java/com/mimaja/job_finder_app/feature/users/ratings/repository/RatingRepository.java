package com.mimaja.job_finder_app.feature.users.ratings.repository;

import com.mimaja.job_finder_app.feature.users.ratings.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
