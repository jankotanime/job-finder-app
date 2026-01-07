package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {
    Job getJobById(UUID jobId);

    List<Job> getJobsAsContractor(UUID userId);

    List<Job> getJobsAsOwner(UUID userId);

    Job createJob(Offer offer);

    void deleteJob(UUID jobId);

    JobDispatcher startJob(UUID jobId);

    JobDispatcher getJobDispatcherByJobId(UUID jobId);

    JobDispatcher reportProblemOwnerTrue(
            UUID jobId, Optional<MultipartFile> photo, String description);

    JobDispatcher reportProblemOwnerFalse(
            UUID jobId, Optional<MultipartFile> photo, String description);

    JobDispatcher reportProblemContractorTrue(
            UUID jobId, Optional<MultipartFile> photo, String description);

    JobDispatcher reportProblemContractorFalse(
            UUID jobId, Optional<MultipartFile> photo, String description);

    Job endJobSuccessfulyOwner(UUID jobId, Optional<MultipartFile> photo, String description);

    Job endJobSuccessfulyContractor(UUID jobId, Optional<MultipartFile> photo, String description);
}
