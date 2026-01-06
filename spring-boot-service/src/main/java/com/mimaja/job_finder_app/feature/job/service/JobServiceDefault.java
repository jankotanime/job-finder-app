package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import com.mimaja.job_finder_app.feature.job.jobphoto.model.JobPhoto;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.shared.adapters.R2FileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
public class JobServiceDefault implements JobService {
    private final JobRepository jobRepository;
    private final OfferService offerService;
    private final FileManagementService fileManagementService;

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
    @Transactional
    public Job createJob(Offer offer) {
        if (offer.getChosenCandidate() == null) {
            throw new BusinessException(BusinessExceptionReason.CANDIDATE_NEED_TO_BE_CHOSEN);
        }
        JobPhoto jobPhoto = null;
        if (offer.getPhoto() != null) {
            jobPhoto = processPhoto(offer.getPhoto());
        }
        Job job = Job.from(offer, jobPhoto);
        offerService.deleteOffer(offer.getId());
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public void deleteJob(UUID jobId) {
        Job job = getOrThrow(jobId);
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public JobDispatcher startJob(UUID jobId) {
        Job job = getOrThrow(jobId);

        if (!job.getStatus().equals(JobStatus.READY)) {
            throw new BusinessException(BusinessExceptionReason.JOB_HAS_ALREADY_STARTED);
        }
        job.setStatus(JobStatus.IN_PROGRESS);
        saveNewJobDispatcher(job, new JobDispatcher());

        return job.getJobDispatcher();
    }

    @Override
    @Transactional
    public JobDispatcher reportProblemContractorFalse(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);

        if (jobDispatcher.getIssueStatusOwner().equals(JobDispatcherIssueStatus.PROBLEM)) {
            jobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NO_PROBLEM);
        }

        if (jobDispatcher.getIssueStatusOwner() == JobDispatcherIssueStatus.NO_PROBLEM) {
            resetJobDispatcher(job, jobDispatcher);
        }

        saveNewJobDispatcher(job, jobDispatcher);
        return jobDispatcher;
    }

    @Override
    @Transactional
    public JobDispatcher reportProblemOwnerFalse(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);

        if (jobDispatcher.getIssueStatusContractor().equals(JobDispatcherIssueStatus.PROBLEM)) {
            jobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);
        }

        if (jobDispatcher.getIssueStatusContractor().equals(JobDispatcherIssueStatus.NO_PROBLEM)) {
            resetJobDispatcher(job, jobDispatcher);
        }
        return jobDispatcher;
    }

    @Override
    @Transactional
    public JobDispatcher reportProblemContractorTrue(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);
        jobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.PROBLEM);

        if (jobDispatcher.getIssueStatusOwner().equals(JobDispatcherIssueStatus.PROBLEM)
                || jobDispatcher
                        .getIssueStatusOwner()
                        .equals(JobDispatcherIssueStatus.NO_PROBLEM)) {
            setJobFailure(job, jobDispatcher);
        }

        saveNewJobDispatcher(job, jobDispatcher);
        return jobDispatcher;
    }

    @Override
    @Transactional
    public JobDispatcher reportProblemOwnerTrue(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);
        jobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.PROBLEM);

        if (jobDispatcher.getFinishedAt() != null
                || jobDispatcher.getIssueStatusContractor().equals(JobDispatcherIssueStatus.PROBLEM)
                || jobDispatcher
                        .getIssueStatusContractor()
                        .equals(JobDispatcherIssueStatus.NO_PROBLEM)) {
            setJobFailure(job, jobDispatcher);
        }

        saveNewJobDispatcher(job, jobDispatcher);
        return jobDispatcher;
    }

    @Override
    @Transactional
    public JobDispatcher getJobDispatcherByJobId(UUID jobId) {
        return getOrThrowJobDispatcher(jobId);
    }

    @Override
    @Transactional
    public Job reportSuccessfulEndJobOwner(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);

        if (jobDispatcher.getFinishedAt() == null) {
            throw new BusinessException(BusinessExceptionReason.JOB_NOT_FINISHED);
        }

        setJobSuccess(job);
        return job;
    }

    @Override
    @Transactional
    public Job reportSuccessfulEndJobContractor(UUID jobId) {
        Job job = getOrThrow(jobId);
        JobDispatcher jobDispatcher = getOrThrowJobDispatcher(jobId);
        jobDispatcher.setFinishedAt(LocalDateTime.now());

        saveNewJobDispatcher(job, jobDispatcher);
        return job;
    }

    private Job getOrThrow(UUID jobId) {
        return jobRepository
                .findById(jobId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND));
    }

    private JobDispatcher getOrThrowJobDispatcher(UUID jobId) {
        Job job = getOrThrow(jobId);
        if (!job.getStatus().equals(JobStatus.IN_PROGRESS)) {
            throw new BusinessException(BusinessExceptionReason.JOB_NOT_IN_PROGRESS);
        }

        JobDispatcher jobDispatcher = job.getJobDispatcher();
        if (jobDispatcher == null) {
            throw new BusinessException(BusinessExceptionReason.JOB_NOT_STARTED);
        }
        return jobDispatcher;
    }

    private JobPhoto processPhoto(OfferPhoto photo) {
        ResponseInputStream<GetObjectResponse> response =
                fileManagementService.getFile(photo.getStorageKey());
        R2FileSource fileSource = new R2FileSource(response, photo.getFileName());
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.JOB_PHOTO);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.uploadFile(fileDetails);

        return JobPhoto.from(photo, fileDetails.storageKey());
    }

    private void saveNewJobDispatcher(Job job, JobDispatcher jobDispatcher) {
        job.setJobDispatcher(jobDispatcher);
        jobRepository.save(job);
    }

    private void resetJobDispatcher(Job job, JobDispatcher jobDispatcher) {
        jobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        jobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        saveNewJobDispatcher(job, jobDispatcher);
    }

    private void setJobFailure(Job job, JobDispatcher jobDispatcher) {
        job.setStatus(JobStatus.FINISHED_FAILURE);
        jobDispatcher.setFinishedAt(LocalDateTime.now());
        saveNewJobDispatcher(job, jobDispatcher);
    }

    private void setJobSuccess(Job job) {
        job.setStatus(JobStatus.FINISHED_SUCCESS);
        jobRepository.save(job);
    }
}
