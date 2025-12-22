package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobUserService {
    private final JobService jobService;
    private final JobMapper jobMapper;
    private final OfferService offerService;

    public JobResponseDto getJobById(JwtPrincipal jwt, UUID jobId) {
        throwErrorIfNotContractorOrOwner(jwt.id(), jobId);
        return jobMapper.toResponseDto(jobService.getJobById(jobId));
    }

    public List<JobResponseDto> getJobsAsContractor(JwtPrincipal jwt) {
        return jobService.getJobsAsContractor(jwt.id()).stream()
                .map(jobMapper::toResponseDto)
                .toList();
    }

    public List<JobResponseDto> getJobsAsOwner(JwtPrincipal jwt) {
        return jobService.getJobsAsOwner(jwt.id()).stream().map(jobMapper::toResponseDto).toList();
    }

    public JobResponseDto createJob(JwtPrincipal jwt, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        throwErrorIfNotOfferOwner(jwt.id(), offerId);
        return jobMapper.toResponseDto(jobService.createJob(offer));
    }

    public void deleteJob(JwtPrincipal jwt, UUID jobId) {
        throwErrorIfNotJobOwner(jwt.id(), jobId);
        jobService.deleteJob(jobId);
    }

    private void throwErrorIfNotContractorOrOwner(UUID userId, UUID jobId) {
        Job job = jobService.getJobById(jobId);
        if (job.getContractor().getId().equals(userId) && !job.getOwner().getId().equals(userId)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER);
        }
    }

    private void throwErrorIfNotOfferOwner(UUID userId, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        if (!offer.getOwner().getId().equals(userId)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }

    private void throwErrorIfNotJobOwner(UUID userId, UUID jobId) {
        Job job = jobService.getJobById(jobId);
        if (!job.getOwner().getId().equals(userId)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }
}
