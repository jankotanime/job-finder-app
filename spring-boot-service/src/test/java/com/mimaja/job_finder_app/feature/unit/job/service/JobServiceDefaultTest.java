package com.mimaja.job_finder_app.feature.unit.job.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.job.service.JobServiceDefault;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.service.FileManagementService;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobServiceDefault - Unit Tests")
public class JobServiceDefaultTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private OfferService offerService;

    @Mock
    private FileManagementService fileManagementService;

    private JobServiceDefault jobService;

    private Job testJob;
    private JobDispatcher testJobDispatcher;
    private Offer testOffer;

    void setUp() {
        testJob = createTestJob();
        testJobDispatcher = createTestJobDispatcher();
        testOffer = createTestOffer();

        jobService = new JobServiceDefault(
            jobRepository,
            offerService,
            fileManagementService
        );
    }

    private Job createTestJob() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        return job;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private JobDispatcher createTestJobDispatcher() {
        JobDispatcher jobDispatcher = new JobDispatcher();
        jobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        jobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        jobDispatcher.setContractiorApprovals(new HashSet<>());
        return jobDispatcher;
    }

    private Offer createTestOffer() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setPhoto(null);
        return offer;
    }

    @Test
    @DisplayName("Should get job by id successfully")
    void testGetJobById_WithValidJobId_ShouldReturnJob() {
        setUp();

        UUID jobId = testJob.getId();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        Job result = jobService.getJobById(jobId);

        assertNotNull(result, "Job should not be null");
        assertThat(result.getId()).isEqualTo(jobId);
        assertThat(result.getStatus()).isEqualTo(JobStatus.READY);

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when getting non-existent job by id")
    void testGetJobById_WithNonExistentJob_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.getJobById(jobId),
            "Should throw BusinessException when job not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job not found")
            .isEqualTo(BusinessExceptionReason.JOB_NOT_FOUND.getCode());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should get jobs as contractor successfully")
    void testGetJobsAsContractor_WithValidUserId_ShouldReturnJobsList() {
        setUp();

        UUID userId = UUID.randomUUID();
        List<Job> expectedJobs = List.of(testJob);

        when(jobRepository.getJobsAsContractor(userId)).thenReturn(expectedJobs);

        List<Job> result = jobService.getJobsAsContractor(userId);

        assertNotNull(result, "Jobs list should not be null");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testJob.getId());

        verify(jobRepository, times(1)).getJobsAsContractor(userId);
    }

    @Test
    @DisplayName("Should get jobs as owner successfully")
    void testGetJobsAsOwner_WithValidUserId_ShouldReturnJobsList() {
        setUp();

        UUID userId = UUID.randomUUID();
        List<Job> expectedJobs = List.of(testJob);

        when(jobRepository.getJobsAsOwner(userId)).thenReturn(expectedJobs);

        List<Job> result = jobService.getJobsAsOwner(userId);

        assertNotNull(result, "Jobs list should not be null");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testJob.getId());

        verify(jobRepository, times(1)).getJobsAsOwner(userId);
    }

    @Test
    @DisplayName("Should create job successfully without photo")
    void testCreateJob_WithValidOfferAndNoPhoto_ShouldReturnCreatedJob() {
        setUp();

        testOffer.setChosenCandidate(createTestUser());
        testOffer.setPhoto(null);

        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.createJob(testOffer);

        assertNotNull(result, "Created job should not be null");

        verify(offerService, times(1)).deleteOffer(testOffer.getId());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating job without chosen candidate")
    void testCreateJob_WithoutChosenCandidate_ShouldThrowBusinessException() {
        setUp();

        testOffer.setChosenCandidate(null);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.createJob(testOffer),
            "Should throw BusinessException when candidate is not chosen"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate candidate need to be chosen")
            .isEqualTo(BusinessExceptionReason.CANDIDATE_NEED_TO_BE_CHOSEN.getCode());
    }

    @Test
    @DisplayName("Should delete job successfully")
    void testDeleteJob_WithValidJobId_ShouldDeleteJob() {
        setUp();

        UUID jobId = testJob.getId();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        jobService.deleteJob(jobId);

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).delete(testJob);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting non-existent job")
    void testDeleteJob_WithNonExistentJob_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.deleteJob(jobId),
            "Should throw BusinessException when job not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job not found")
            .isEqualTo(BusinessExceptionReason.JOB_NOT_FOUND.getCode());

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(0)).delete(any(Job.class));
    }

    @Test
    @DisplayName("Should start job successfully")
    void testStartJob_WithValidJobIdAndReadyStatus_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        testJob.setJobDispatcher(null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.startJob(jobId);

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.IN_PROGRESS);

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when starting job that is not ready")
    void testStartJob_WithJobNotReady_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.startJob(jobId),
            "Should throw BusinessException when job has already started"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job has already started")
            .isEqualTo(BusinessExceptionReason.JOB_HAS_ALREADY_STARTED.getCode());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should get job dispatcher by job id successfully")
    void testGetJobDispatcherByJobId_WithValidJobId_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        JobDispatcher result = jobService.getJobDispatcherByJobId(jobId);

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result).isEqualTo(testJobDispatcher);

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when job dispatcher not found")
    void testGetJobDispatcherByJobId_WithNoJobDispatcher_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.getJobDispatcherByJobId(jobId),
            "Should throw BusinessException when job dispatcher not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job not started")
            .isEqualTo(BusinessExceptionReason.JOB_NOT_STARTED.getCode());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem contractor false successfully")
    void testReportProblemContractorFalse_WithValidData_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem here");

        assertNotNull(result, "JobDispatcher should not be null");

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem contractor true successfully")
    void testReportProblemContractorTrue_WithValidData_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorTrue(jobId, Optional.empty(), "There is a problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner false successfully")
    void testReportProblemOwnerFalse_WithValidData_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerFalse(jobId, Optional.empty(), "No problem");

        assertNotNull(result, "JobDispatcher should not be null");

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner true successfully")
    void testReportProblemOwnerTrue_WithValidData_ShouldReturnJobDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem found");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should end job successfully for owner when job is finished")
    void testEndJobSuccessfulyOwner_WithFinishedJob_ShouldReturnSuccessfulJob() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.endJobSuccessfulyOwner(jobId, Optional.empty(), "Job completed successfully");

        assertNotNull(result, "Job should not be null");
        assertThat(result.getStatus()).isEqualTo(JobStatus.FINISHED_SUCCESS);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when ending job that is not finished")
    void testEndJobSuccessfulyOwner_WithUnfinishedJob_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.endJobSuccessfulyOwner(jobId, Optional.empty(), "Job completed"),
            "Should throw BusinessException when job is not finished"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job not finished")
            .isEqualTo(BusinessExceptionReason.JOB_NOT_FINISHED.getCode());

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should end job successfully for contractor")
    void testEndJobSuccessfulyContractor_WithValidJobId_ShouldReturnJob() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.endJobSuccessfulyContractor(jobId, Optional.empty(), "Work completed");

        assertNotNull(result, "Job should not be null");
        assertThat(result.getJobDispatcher().getFinishedAt()).isNotNull();

        verify(jobRepository, times(2)).findById(jobId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when job is not in progress")
    void testGetJobDispatcherByJobId_WithJobNotInProgress_ShouldThrowBusinessException() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> jobService.getJobDispatcherByJobId(jobId),
            "Should throw BusinessException when job is not in progress"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate job not in progress")
            .isEqualTo(BusinessExceptionReason.JOB_NOT_IN_PROGRESS.getCode());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    @DisplayName("Should create job successfully with photo")
    void testCreateJob_WithValidOfferAndPhoto_ShouldReturnCreatedJobWithPhoto() {
        setUp();

        testOffer.setChosenCandidate(createTestUser());
        OfferPhoto offerPhoto = new OfferPhoto();
        offerPhoto.setStorageKey("test-storage-key");
        offerPhoto.setFileName("test.jpg");
        testOffer.setPhoto(offerPhoto);

        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(fileManagementService.getFile(any())).thenReturn(null);
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.createJob(testOffer);

        assertNotNull(result, "Created job should not be null");

        verify(offerService, times(1)).deleteOffer(testOffer.getId());
        verify(jobRepository, times(1)).save(any(Job.class));
        verify(fileManagementService, times(1)).getFile(any());
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should report problem contractor false when owner status is PROBLEM")
    void testReportProblemContractorFalse_WithOwnerProblem_ShouldSetContractorNoProblm() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.NO_PROBLEM);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem contractor false and reset when owner is NO_PROBLEM")
    void testReportProblemContractorFalse_WithOwnerNoProblm_ShouldResetDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NONE);
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.NONE);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem contractor true and set job failure when owner has PROBLEM")
    void testReportProblemContractorTrue_WithOwnerProblem_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem contractor true and set job failure when owner has NO_PROBLEM")
    void testReportProblemContractorTrue_WithOwnerNoProblem_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner false when contractor status is PROBLEM")
    void testReportProblemOwnerFalse_WithContractorProblem_ShouldSetOwnerNoProblem() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerFalse(jobId, Optional.empty(), "No problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NO_PROBLEM);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner false and reset when contractor is NO_PROBLEM")
    void testReportProblemOwnerFalse_WithContractorNoProblem_ShouldResetDispatcher() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NO_PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerFalse(jobId, Optional.empty(), "No problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NONE);
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.NONE);

        verify(jobRepository, times(2)).findById(jobId);
        verify(jobRepository, times(2)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should report problem owner true and set job failure when contractor has PROBLEM")
    void testReportProblemOwnerTrue_WithContractorProblem_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner true and set job failure when job is already finished")
    void testReportProblemOwnerTrue_WithFinishedJob_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should end job successfully for owner and save with success status")
    void testEndJobSuccessfulyOwner_WithFinishedJob_ShouldSaveJobWithSuccessStatus() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.endJobSuccessfulyOwner(jobId, Optional.empty(), "Completed");

        assertNotNull(result, "Job should not be null");
        assertThat(result.getStatus()).isEqualTo(JobStatus.FINISHED_SUCCESS);

        verify(jobRepository, times(2)).findById(jobId);
        verify(jobRepository, times(2)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should report problem contractor false with photo")
    void testReportProblemContractorFalse_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorFalse(jobId, Optional.of(photo), "No problem with photo");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getContractiorApprovals()).isNotEmpty();

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should report problem contractor true with photo")
    void testReportProblemContractorTrue_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorTrue(jobId, Optional.of(photo), "Problem with photo");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);
        assertThat(result.getContractiorApprovals()).isNotEmpty();

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should report problem owner false with photo")
    void testReportProblemOwnerFalse_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerFalse(jobId, Optional.of(photo), "No problem from owner");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getContractiorApprovals()).isNotEmpty();

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should report problem owner true with photo")
    void testReportProblemOwnerTrue_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerTrue(jobId, Optional.of(photo), "Problem from owner with photo");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);
        assertThat(result.getContractiorApprovals()).isNotEmpty();

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should end job successfully for owner with photo")
    void testEndJobSuccessfulyOwner_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.endJobSuccessfulyOwner(jobId, Optional.of(photo), "Job finished successfully with photo");

        assertNotNull(result, "Job should not be null");
        assertThat(result.getStatus()).isEqualTo(JobStatus.FINISHED_SUCCESS);

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should end job successfully for contractor with photo")
    void testEndJobSuccessfulyContractor_WithPhoto_ShouldAddApprovalWithPhoto() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);

        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job result = jobService.endJobSuccessfulyContractor(jobId, Optional.of(photo), "Work completed with photo");

        assertNotNull(result, "Job should not be null");
        assertThat(result.getJobDispatcher().getFinishedAt()).isNotNull();

        verify(jobRepository, times(2)).findById(jobId);
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    @DisplayName("Should report problem contractor true and set job failure when owner has NO_PROBLEM status")
    void testReportProblemContractorTrue_WithOwnerNoProblemStatus_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem detected");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
        assertThat(result.getFinishedAt()).isNotNull();

        verify(jobRepository, times(2)).findById(jobId);
    }

    @Test
    @DisplayName("Should report problem owner true and set job failure when contractor has NO_PROBLEM status")
    void testReportProblemOwnerTrue_WithContractorNoProblemStatus_ShouldSetJobFailure() {
        setUp();

        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NO_PROBLEM);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobDispatcher result = jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Owner detected problem");

        assertNotNull(result, "JobDispatcher should not be null");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
        assertThat(result.getFinishedAt()).isNotNull();

        verify(jobRepository, times(2)).findById(jobId);
    }
}
