package com.mimaja.job_finder_app.feature.jobs.repository;

import com.mimaja.job_finder_app.feature.jobs.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
