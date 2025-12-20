package com.mimaja.job_finder_app.feature.cv.repository;

import com.mimaja.job_finder_app.feature.cv.model.Cv;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CvRepository extends JpaRepository<Cv, UUID> {
    List<Cv> findAllCvsByUserId(UUID userId);
}
