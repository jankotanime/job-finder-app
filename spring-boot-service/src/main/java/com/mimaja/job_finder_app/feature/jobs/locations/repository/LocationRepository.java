package com.mimaja.job_finder_app.feature.jobs.locations.repository;

import com.mimaja.job_finder_app.feature.jobs.locations.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
