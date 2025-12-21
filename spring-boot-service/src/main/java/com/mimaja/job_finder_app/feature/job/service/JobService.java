package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import java.util.List;
import java.util.UUID;

public interface JobService {
    Job getJobById(UUID jobId);

    List<Job> getJobsAsContractor(UUID userId);

    List<Job> getJobsAsOwner(UUID userId);

    Job createJob(Offer offer);

    void deleteJob(UUID jobId);
}
