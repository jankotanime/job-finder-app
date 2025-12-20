package com.mimaja.job_finder_app.feature.job.repository;

import com.mimaja.job_finder_app.feature.job.model.Job;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> getJobsAsContractor(UUID userId);

    List<Job> getJobsAsOwner(UUID userId);
}
