package com.mimaja.job_finder_app.feature.job.repository;

import com.mimaja.job_finder_app.feature.job.model.Job;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, UUID> {
    @Query("SELECT j FROM Job j WHERE j.contractor.id = :userId")
    List<Job> getJobsAsContractor(UUID userId);

    @Query("SELECT j FROM Job j WHERE j.owner.id = :userId")
    List<Job> getJobsAsOwner(UUID userId);
}
