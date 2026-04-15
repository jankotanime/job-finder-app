package com.mimaja.job_finder_app.feature.unit.job.service;

import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestFileDetails;
import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestJob;
import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestJobDispatcher;
import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestOffer;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.job.service.JobServiceDefault;
import com.mimaja.job_finder_app.feature.job.utils.JobWebSocketSignalsHandler;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class JobServiceDefaultTest {
    @Mock private JobRepository jobRepository;
    @Mock private OfferService offerService;
    @Mock private FileManagementService fileManagementService;
    @Mock private JobWebSocketSignalsHandler jobWebSocketSignalsHandler;

    private JobServiceDefault jobService;
    private Job testJob;
    private JobDispatcher testJobDispatcher;
    private Offer testOffer;

    @BeforeEach
    void setUp() {
        testJob = createTestJob();
        testJobDispatcher = createTestJobDispatcher();
        testOffer = createTestOffer();
        jobService = new JobServiceDefault(jobRepository, offerService, fileManagementService, jobWebSocketSignalsHandler);
    }

    // --- getJobById ---

    @Test
    void getJobById_shouldReturnJob_whenJobExists() {
        UUID jobId = testJob.getId();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        Job result = jobService.getJobById(jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobById_shouldCallFindById_whenJobExists() {
        UUID jobId = testJob.getId();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        jobService.getJobById(jobId);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void getJobById_shouldThrowExceptionWithJobNotFoundCode_whenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        BusinessException exception =
                assertThrows(BusinessException.class, () -> jobService.getJobById(jobId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.JOB_NOT_FOUND.getCode());
    }

    @Test
    void getJobById_shouldCallFindById_whenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> jobService.getJobById(jobId));
        verify(jobRepository, times(1)).findById(jobId);
    }

    // --- getJobsAsContractor ---

    @Test
    void getJobsAsContractor_shouldReturnExpectedJobs_whenUserHasJobs() {
        UUID userId = UUID.randomUUID();
        when(jobRepository.getJobsAsContractor(userId)).thenReturn(List.of(testJob));
        List<Job> result = jobService.getJobsAsContractor(userId);
        assertThat(result).hasSize(1);
    }

    @Test
    void getJobsAsContractor_shouldCallGetJobsAsContractor_whenUserHasJobs() {
        UUID userId = UUID.randomUUID();
        when(jobRepository.getJobsAsContractor(userId)).thenReturn(List.of(testJob));
        jobService.getJobsAsContractor(userId);
        verify(jobRepository, times(1)).getJobsAsContractor(userId);
    }

    // --- getJobsAsOwner ---

    @Test
    void getJobsAsOwner_shouldReturnExpectedJobs_whenUserHasJobs() {
        UUID userId = UUID.randomUUID();
        when(jobRepository.getJobsAsOwner(userId)).thenReturn(List.of(testJob));
        List<Job> result = jobService.getJobsAsOwner(userId);
        assertThat(result).hasSize(1);
    }

    @Test
    void getJobsAsOwner_shouldCallGetJobsAsOwner_whenUserHasJobs() {
        UUID userId = UUID.randomUUID();
        when(jobRepository.getJobsAsOwner(userId)).thenReturn(List.of(testJob));
        jobService.getJobsAsOwner(userId);
        verify(jobRepository, times(1)).getJobsAsOwner(userId);
    }

    // --- createJob ---

    @Test
    void createJob_shouldReturnNonNullJob_whenOfferHasNoPhoto() {
        testOffer.setChosenCandidate(createTestUser());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        Job result = jobService.createJob(testOffer);
        assertThat(result).isNotNull();
    }

    @Test
    void createJob_shouldDeleteOffer_whenOfferHasNoPhoto() {
        testOffer.setChosenCandidate(createTestUser());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.createJob(testOffer);
        verify(offerService, times(1)).deleteOffer(testOffer.getId());
    }

    @Test
    void createJob_shouldSaveJob_whenOfferHasNoPhoto() {
        testOffer.setChosenCandidate(createTestUser());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.createJob(testOffer);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void createJob_shouldThrowExceptionWithCandidateNotChosenCode_whenCandidateNotSet() {
        testOffer.setChosenCandidate(null);
        BusinessException exception =
                assertThrows(BusinessException.class, () -> jobService.createJob(testOffer));
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CANDIDATE_NEED_TO_BE_CHOSEN.getCode());
    }

    @Test
    void createJob_shouldGetFileFromStorage_whenOfferHasPhoto() {
        testOffer.setChosenCandidate(createTestUser());
        testOffer.setPhoto(createTestOfferPhoto());
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.createJob(testOffer);
        verify(fileManagementService, times(1)).getFile(any());
    }

    @Test
    void createJob_shouldUploadFile_whenOfferHasPhoto() {
        testOffer.setChosenCandidate(createTestUser());
        testOffer.setPhoto(createTestOfferPhoto());
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.createJob(testOffer);
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    // --- deleteJob ---

    @Test
    void deleteJob_shouldCallFindById_whenDeletingJob() {
        UUID jobId = testJob.getId();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        jobService.deleteJob(jobId);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void deleteJob_shouldDeleteJob_whenJobExists() {
        UUID jobId = testJob.getId();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        jobService.deleteJob(jobId);
        verify(jobRepository, times(1)).delete(testJob);
    }

    @Test
    void deleteJob_shouldThrowExceptionWithJobNotFoundCode_whenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        BusinessException exception =
                assertThrows(BusinessException.class, () -> jobService.deleteJob(jobId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.JOB_NOT_FOUND.getCode());
    }

    @Test
    void deleteJob_shouldCallFindById_whenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> jobService.deleteJob(jobId));
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void deleteJob_shouldNotDeleteJob_whenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> jobService.deleteJob(jobId));
        verify(jobRepository, never()).delete(any(Job.class));
    }

    // --- startJob ---

    @Test
    void startJob_shouldReturnNonNullDispatcher_whenJobIsReady() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        testJob.setJobDispatcher(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        JobDispatcher result = jobService.startJobOwner(jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void startJob_shouldSetStatusToInProgress_whenJobIsReady() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        testJob.setJobDispatcher(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.startJobOwner(jobId);
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.READY);
    }

    @Test
    void startJob_shouldCallFindById_whenStartingJob() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        testJob.setJobDispatcher(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.startJobOwner(jobId);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void startJob_shouldSaveJob_whenStartingJob() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        testJob.setJobDispatcher(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.startJobOwner(jobId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void startJob_shouldThrowExceptionWithJobAlreadyStartedCode_whenJobInProgress() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        BusinessException exception =
                assertThrows(BusinessException.class, () -> jobService.startJobOwner(jobId));
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.JOB_NOT_UNREADY.getCode());
    }

    @Test
    void startJob_shouldCallFindById_whenJobNotReady() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        assertThrows(BusinessException.class, () -> jobService.startJobOwner(jobId));
        verify(jobRepository, times(1)).findById(jobId);
    }

    // --- getJobDispatcherByJobId ---

    @Test
    void getJobDispatcherByJobId_shouldReturnDispatcher_whenJobIsInProgress() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        JobDispatcher result = jobService.getJobDispatcherByJobId(jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobDispatcherByJobId_shouldCallFindById_whenJobIsInProgress() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        jobService.getJobDispatcherByJobId(jobId);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void getJobDispatcherByJobId_shouldThrowExceptionWithJobNotStartedCode_whenNoDispatcher() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> jobService.getJobDispatcherByJobId(jobId));
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.JOB_NOT_STARTED.getCode());
    }

    @Test
    void
            getJobDispatcherByJobId_shouldThrowExceptionWithJobNotInProgressCode_whenJobNotInProgress() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.READY);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> jobService.getJobDispatcherByJobId(jobId));
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.JOB_NOT_IN_PROGRESS.getCode());
    }

    // --- reportProblemContractorFalse ---

    @Test
    void reportProblemContractorFalse_shouldReturnNonNullDispatcher_whenOwnerStatusIsNone() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem");
        assertThat(result).isNotNull();
    }

    @Test
    void
            reportProblemContractorFalse_shouldSetContractorNoProblemStatus_whenOwnerStatusIsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem");
        assertThat(result.getIssueStatusContractor())
                .isEqualTo(JobDispatcherIssueStatus.NO_PROBLEM);
    }

    @Test
    void reportProblemContractorFalse_shouldResetDispatcher_whenOwnerStatusIsNoProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemContractorFalse(jobId, Optional.empty(), "No problem");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NONE);
    }

    @Test
    void reportProblemContractorFalse_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        JobDispatcher result =
                jobService.reportProblemContractorFalse(
                        jobId, Optional.of(createMockPhoto()), "No problem");
        assertThat(result.getContractiorApprovals()).isNotEmpty();
    }

    @Test
    void reportProblemContractorFalse_shouldUploadFile_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.reportProblemContractorFalse(
                jobId, Optional.of(createMockPhoto()), "No problem");
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    // --- reportProblemContractorTrue ---

    @Test
    void
            reportProblemContractorTrue_shouldSetContractorProblemStatus_whenContractorReportsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem");
        assertThat(result.getIssueStatusContractor()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);
    }

    @Test
    void reportProblemContractorTrue_shouldSetJobFailure_whenOwnerStatusIsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
    }

    @Test
    void reportProblemContractorTrue_shouldSetJobFailure_whenOwnerStatusIsNoProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NO_PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        jobService.reportProblemContractorTrue(jobId, Optional.empty(), "Problem");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
    }

    @Test
    void reportProblemContractorTrue_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        JobDispatcher result =
                jobService.reportProblemContractorTrue(
                        jobId, Optional.of(createMockPhoto()), "Problem");
        assertThat(result.getContractiorApprovals()).isNotEmpty();
    }

    // --- reportProblemOwnerFalse ---

    @Test
    void reportProblemOwnerFalse_shouldSetOwnerNoProblemStatus_whenContractorStatusIsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemOwnerFalse(jobId, Optional.empty(), "No problem");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NO_PROBLEM);
    }

    @Test
    void reportProblemOwnerFalse_shouldResetDispatcher_whenContractorStatusIsNoProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NO_PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemOwnerFalse(jobId, Optional.empty(), "No problem");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.NONE);
    }

    @Test
    void reportProblemOwnerFalse_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        JobDispatcher result =
                jobService.reportProblemOwnerFalse(
                        jobId, Optional.of(createMockPhoto()), "No problem");
        assertThat(result.getContractiorApprovals()).isNotEmpty();
    }

    // --- reportProblemOwnerTrue ---

    @Test
    void reportProblemOwnerTrue_shouldSetOwnerProblemStatus_whenOwnerReportsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        setupJobFindAndSaveMocks(jobId);
        JobDispatcher result =
                jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");
        assertThat(result.getIssueStatusOwner()).isEqualTo(JobDispatcherIssueStatus.PROBLEM);
    }

    @Test
    void reportProblemOwnerTrue_shouldSetJobFailure_whenContractorStatusIsProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
    }

    @Test
    void reportProblemOwnerTrue_shouldSetJobFailure_whenContractorStatusIsNoProblem() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NO_PROBLEM);
        setupJobFindAndSaveMocks(jobId);
        jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
    }

    @Test
    void reportProblemOwnerTrue_shouldSetJobFailure_whenJobIsAlreadyFinished() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());
        testJobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        setupJobFindAndSaveMocks(jobId);
        jobService.reportProblemOwnerTrue(jobId, Optional.empty(), "Problem");
        assertThat(testJob.getStatus()).isEqualTo(JobStatus.FINISHED_FAILURE);
    }

    @Test
    void reportProblemOwnerTrue_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        JobDispatcher result =
                jobService.reportProblemOwnerTrue(jobId, Optional.of(createMockPhoto()), "Problem");
        assertThat(result.getContractiorApprovals()).isNotEmpty();
    }

    // --- endJobSuccessfulyOwner ---

    @Test
    void endJobSuccessfulyOwner_shouldReturnJobWithSuccessStatus_whenJobIsFinished() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());
        setupJobFindAndSaveMocks(jobId);
        Job result = jobService.endJobSuccessfulyOwner(jobId, Optional.empty(), "Completed");
        assertThat(result.getStatus()).isEqualTo(JobStatus.FINISHED_SUCCESS);
    }

    @Test
    void endJobSuccessfulyOwner_shouldCallSave_whenJobIsFinished() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());
        setupJobFindAndSaveMocks(jobId);
        jobService.endJobSuccessfulyOwner(jobId, Optional.empty(), "Completed");
        verify(jobRepository, times(2)).save(any(Job.class));
    }

    @Test
    void endJobSuccessfulyOwner_shouldThrowExceptionWithJobNotFinishedCode_whenJobNotFinished() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(null);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () ->
                                jobService.endJobSuccessfulyOwner(
                                        jobId, Optional.empty(), "Completed"));
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.JOB_NOT_FINISHED.getCode());
    }

    @Test
    void endJobSuccessfulyOwner_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        testJobDispatcher.setFinishedAt(LocalDateTime.now());
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.endJobSuccessfulyOwner(jobId, Optional.of(createMockPhoto()), "Completed");
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    // --- endJobSuccessfulyContractor ---

    @Test
    void endJobSuccessfulyContractor_shouldSetFinishedAt_whenContractorEndsJob() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        setupJobFindAndSaveMocks(jobId);
        Job result =
                jobService.endJobSuccessfulyContractor(jobId, Optional.empty(), "Work completed");
        assertThat(result.getJobDispatcher().getFinishedAt()).isNotNull();
    }

    @Test
    void endJobSuccessfulyContractor_shouldCallSave_whenContractorEndsJob() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        setupJobFindAndSaveMocks(jobId);
        jobService.endJobSuccessfulyContractor(jobId, Optional.empty(), "Work completed");
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void endJobSuccessfulyContractor_shouldAddApproval_whenPhotoProvided() {
        UUID jobId = testJob.getId();
        testJob.setStatus(JobStatus.IN_PROGRESS);
        testJob.setJobDispatcher(testJobDispatcher);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        jobService.endJobSuccessfulyContractor(
                jobId, Optional.of(createMockPhoto()), "Work completed");
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    private void setupJobFindAndSaveMocks(UUID jobId) {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
    }

    private MultipartFile createMockPhoto() {
        return org.mockito.Mockito.mock(MultipartFile.class);
    }

    private OfferPhoto createTestOfferPhoto() {
        OfferPhoto photo = new OfferPhoto();
        photo.setStorageKey("test-storage-key");
        photo.setFileName("test.jpg");
        return photo;
    }
}
