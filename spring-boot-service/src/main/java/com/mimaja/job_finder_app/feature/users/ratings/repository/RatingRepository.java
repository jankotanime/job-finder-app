package com.mimaja.job_finder_app.feature.users.ratings.repository;

import com.mimaja.job_finder_app.feature.users.ratings.model.Rating;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, UUID> {}
