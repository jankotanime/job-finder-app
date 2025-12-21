package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceDefault implements JobService {
    private final JobRepository jobRepository;
    private final OfferService offerService;

    @Override
    public Job getJobById(UUID jobId) {
        return getOrThrow(jobId);
    }

    @Override
    public List<Job> getJobsAsContractor(UUID userId) {
        return jobRepository.getJobsAsContractor(userId);
    }

    @Override
    public List<Job> getJobsAsOwner(UUID userId) {
        return jobRepository.getJobsAsOwner(userId);
    }

    @Override
    public Job createJob(Offer offer) {
        if (offer.getChosenCandidate() == null) {
            throw new BusinessException(BusinessExceptionReason.CANDIDATE_NEED_TO_BE_CHOSEN);
        }
        Job job = Job.from(offer);
        offerService.deleteOffer(offer.getId());
        return jobRepository.save(job);
    }

    @Override
    public void deleteJob(UUID jobId) {
        Job job = getOrThrow(jobId);
        jobRepository.delete(job);
    }

    private Job getOrThrow(UUID jobId) {
        return jobRepository
                .findById(jobId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND));
    }
}
