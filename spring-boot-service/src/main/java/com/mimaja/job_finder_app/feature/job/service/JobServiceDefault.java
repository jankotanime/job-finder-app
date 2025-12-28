package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobphoto.model.JobPhoto;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.shared.adapters.R2FileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
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

    private Job getOrThrow(UUID jobId) {
        return jobRepository
                .findById(jobId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND));
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
}
