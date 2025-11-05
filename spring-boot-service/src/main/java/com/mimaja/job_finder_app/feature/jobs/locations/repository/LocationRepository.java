package com.mimaja.job_finder_app.feature.jobs.locations.repository;

import com.mimaja.job_finder_app.feature.jobs.locations.model.Location;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, UUID> {}
