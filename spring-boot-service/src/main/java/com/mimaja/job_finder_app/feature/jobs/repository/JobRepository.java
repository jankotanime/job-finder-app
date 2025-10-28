package com.mimaja.job_finder_app.feature.jobs.repository;import com.mimaja.job_finder_app.feature.jobs.model.Job;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, UUID> {}
