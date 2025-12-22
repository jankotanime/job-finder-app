package com.mimaja.job_finder_app.feature.job.service;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobphoto.model.JobPhoto;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class JobServiceDefault implements JobService {
    private final JobRepository jobRepository;
    private final OfferService offerService;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

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
        Set<JobPhoto> photos =
                offer.getPhotos().stream().map(this::processPhoto).collect(Collectors.toSet());
        Job job = Job.from(offer, photos);
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
        GetObjectRequest reqGet =
                GetObjectRequest.builder().bucket(bucket).key(photo.getStorageKey()).build();

        String folder = "photos/job-photos";
        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), photo.getFileName());

        try {
            ResponseInputStream<GetObjectResponse> responseGet = s3Client.getObject(reqGet);
            String contentType = responseGet.response().contentType();
            byte[] bytes = responseGet.readAllBytes();
            PutObjectRequest req =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .build();
            responseGet.close();
            s3Client.putObject(req, RequestBody.fromBytes(bytes));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION);
        }

        return JobPhoto.from(photo, key);
    }
}
