package com.mimaja.job_finder_app.feature.unit.job.service;

import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestJobDispatcher;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithProfilePhoto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.dto.JobDispatcherResponseDto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.service.JobService;
import com.mimaja.job_finder_app.feature.job.service.JobUserService;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
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
class JobUserServiceTest {
    @Mock private JobService jobService;
    @Mock private JobMapper jobMapper;
    @Mock private OfferService offerService;

    private JobUserService jobUserService;
    private Job testJob;
    private Offer testOffer;
    private User testOwner;
    private User testContractor;
    private JobResponseDto testJobResponseDto;
    private JobDispatcher testJobDispatcher;
    private JwtPrincipal testJwtPrincipal;

    @BeforeEach
    void setUp() {
        testOwner = createTestUserWithProfilePhoto();
        testContractor = createTestUserWithProfilePhoto();
        testJob = createJobWithUsers();
        testOffer = createOfferWithUsers();
        testJobResponseDto = createMockJobResponseDto();
        testJobDispatcher = createTestJobDispatcher();
        testJwtPrincipal = JwtPrincipal.from(testOwner);
        jobUserService = new JobUserService(jobService, jobMapper, offerService);
    }

    // --- getJobById ---

    @Test
    void getJobById_shouldReturnNonNullResponseDto_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        JobResponseDto result = jobUserService.getJobById(testJwtPrincipal, jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobById_shouldCallGetJobById_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.getJobById(testJwtPrincipal, jobId);
        verify(jobService, times(2)).getJobById(jobId);
    }

    @Test
    void getJobById_shouldCallMapper_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.getJobById(testJwtPrincipal, jobId);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    void getJobById_shouldReturnNonNullResponseDto_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        JwtPrincipal contractorPrincipal = JwtPrincipal.from(testContractor);
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        JobResponseDto result = jobUserService.getJobById(contractorPrincipal, jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobById_shouldThrowBusinessException_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.getJobById(JwtPrincipal.from(unauthorizedUser), jobId));
    }

    // --- getJobsAsContractor ---

    @Test
    void getJobsAsContractor_shouldReturnExpectedList_whenContractorHasJobs() {
        when(jobService.getJobsAsContractor(testJwtPrincipal.id())).thenReturn(List.of(testJob));
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        List<JobResponseDto> result = jobUserService.getJobsAsContractor(testJwtPrincipal);
        assertThat(result).hasSize(1);
    }

    @Test
    void getJobsAsContractor_shouldCallGetJobsAsContractor_whenGettingJobs() {
        when(jobService.getJobsAsContractor(testJwtPrincipal.id())).thenReturn(List.of(testJob));
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.getJobsAsContractor(testJwtPrincipal);
        verify(jobService, times(1)).getJobsAsContractor(testJwtPrincipal.id());
    }

    @Test
    void getJobsAsContractor_shouldReturnEmptyList_whenContractorHasNoJobs() {
        when(jobService.getJobsAsContractor(testJwtPrincipal.id())).thenReturn(List.of());
        List<JobResponseDto> result = jobUserService.getJobsAsContractor(testJwtPrincipal);
        assertThat(result).isEmpty();
    }

    // --- getJobsAsOwner ---

    @Test
    void getJobsAsOwner_shouldReturnExpectedList_whenOwnerHasJobs() {
        when(jobService.getJobsAsOwner(testJwtPrincipal.id())).thenReturn(List.of(testJob));
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        List<JobResponseDto> result = jobUserService.getJobsAsOwner(testJwtPrincipal);
        assertThat(result).hasSize(1);
    }

    @Test
    void getJobsAsOwner_shouldCallGetJobsAsOwner_whenGettingJobs() {
        when(jobService.getJobsAsOwner(testJwtPrincipal.id())).thenReturn(List.of(testJob));
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.getJobsAsOwner(testJwtPrincipal);
        verify(jobService, times(1)).getJobsAsOwner(testJwtPrincipal.id());
    }

    @Test
    void getJobsAsOwner_shouldReturnEmptyList_whenOwnerHasNoJobs() {
        when(jobService.getJobsAsOwner(testJwtPrincipal.id())).thenReturn(List.of());
        List<JobResponseDto> result = jobUserService.getJobsAsOwner(testJwtPrincipal);
        assertThat(result).isEmpty();
    }

    // --- createJob ---

    @Test
    void createJob_shouldReturnNonNullResponseDto_whenOwnerCreatesJob() {
        UUID offerId = testOffer.getId();
        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(jobService.createJob(testOffer)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        JobResponseDto result = jobUserService.createJob(testJwtPrincipal, offerId);
        assertThat(result).isNotNull();
    }

    @Test
    void createJob_shouldCallCreateJob_whenOwnerCreatesJob() {
        UUID offerId = testOffer.getId();
        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(jobService.createJob(testOffer)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.createJob(testJwtPrincipal, offerId);
        verify(jobService, times(1)).createJob(testOffer);
    }

    @Test
    void createJob_shouldThrowBusinessException_whenCalledByNonOwner() {
        UUID offerId = testOffer.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.createJob(JwtPrincipal.from(unauthorizedUser), offerId));
    }

    @Test
    void createJob_shouldNotCallCreateJob_whenCalledByNonOwner() {
        UUID offerId = testOffer.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.createJob(JwtPrincipal.from(unauthorizedUser), offerId));
        verify(jobService, never()).createJob(testOffer);
    }

    // --- deleteJob ---

    @Test
    void deleteJob_shouldCallDeleteJob_whenOwnerDeletesJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        jobUserService.deleteJob(testJwtPrincipal, jobId);
        verify(jobService, times(1)).deleteJob(jobId);
    }

    @Test
    void deleteJob_shouldCallGetJobById_whenDeletingJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        jobUserService.deleteJob(testJwtPrincipal, jobId);
        verify(jobService, times(1)).getJobById(jobId);
    }

    @Test
    void deleteJob_shouldThrowBusinessException_whenCalledByNonOwner() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.deleteJob(JwtPrincipal.from(unauthorizedUser), jobId));
    }

    @Test
    void deleteJob_shouldNotCallDeleteJob_whenCalledByNonOwner() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.deleteJob(JwtPrincipal.from(unauthorizedUser), jobId));
        verify(jobService, never()).deleteJob(jobId);
    }

    // --- startJob ---

    @Test
    void startJob_shouldReturnNonNullResponseDto_whenOwnerStartsJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.startJobOwner(jobId)).thenReturn(testJobDispatcher);
        JobDispatcherResponseDto result = jobUserService.startJob(testOwner.getId(), jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void startJob_shouldCallStartJob_whenOwnerStartsJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.startJobOwner(jobId)).thenReturn(testJobDispatcher);
        jobUserService.startJob(testOwner.getId(), jobId);
        verify(jobService, times(1)).startJobOwner(jobId);
    }

    @Test
    void startJob_shouldReturnNonNullResponseDto_whenContractorStartsJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.startJobContractor(jobId)).thenReturn(testJobDispatcher);
        JobDispatcherResponseDto result = jobUserService.startJob(testContractor.getId(), jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void startJob_shouldCallStartJobContractor_whenContractorStartsJob() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.startJobContractor(jobId)).thenReturn(testJobDispatcher);
        jobUserService.startJob(testContractor.getId(), jobId);
        verify(jobService, times(1)).startJobContractor(jobId);
    }

    @Test
    void startJob_shouldThrowBusinessException_whenCalledByNonOwner() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.startJob(unauthorizedUser.getId(), jobId));
    }

    @Test
    void startJob_shouldNotCallStartJob_whenCalledByNonOwner() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.startJob(unauthorizedUser.getId(), jobId));
        verify(jobService, never()).startJobOwner(jobId);
    }

    // --- getJobDispatcher ---

    @Test
    void getJobDispatcher_shouldReturnNonNullResponseDto_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.getJobDispatcherByJobId(jobId)).thenReturn(testJobDispatcher);
        JobDispatcherResponseDto result = jobUserService.getJobDispatcher(testOwner.getId(), jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobDispatcher_shouldReturnNonNullResponseDto_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.getJobDispatcherByJobId(jobId)).thenReturn(testJobDispatcher);
        JobDispatcherResponseDto result =
                jobUserService.getJobDispatcher(testContractor.getId(), jobId);
        assertThat(result).isNotNull();
    }

    @Test
    void getJobDispatcher_shouldThrowBusinessException_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.getJobDispatcher(unauthorizedUser.getId(), jobId));
    }

    @Test
    void getJobDispatcher_shouldNotCallGetJobDispatcher_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () -> jobUserService.getJobDispatcher(unauthorizedUser.getId(), jobId));
        verify(jobService, never()).getJobDispatcherByJobId(jobId);
    }

    // --- reportProblemTrue ---

    @Test
    void reportProblemTrue_shouldReturnNonNullResponseDto_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Problem";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorTrue(jobId, photo, description))
                .thenReturn(testJobDispatcher);
        JobDispatcherResponseDto result =
                jobUserService.reportProblemTrue(testContractor.getId(), jobId, photo, description);
        assertThat(result).isNotNull();
    }

    @Test
    void reportProblemTrue_shouldCallReportProblemContractorTrue_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Problem";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorTrue(jobId, photo, description))
                .thenReturn(testJobDispatcher);
        jobUserService.reportProblemTrue(testContractor.getId(), jobId, photo, description);
        verify(jobService, times(1)).reportProblemContractorTrue(jobId, photo, description);
    }

    @Test
    void reportProblemTrue_shouldCallReportProblemOwnerTrue_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Problem";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerTrue(jobId, photo, description))
                .thenReturn(testJobDispatcher);
        jobUserService.reportProblemTrue(testOwner.getId(), jobId, photo, description);
        verify(jobService, times(1)).reportProblemOwnerTrue(jobId, photo, description);
    }

    @Test
    void reportProblemTrue_shouldThrowBusinessException_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () ->
                        jobUserService.reportProblemTrue(
                                unauthorizedUser.getId(), jobId, Optional.empty(), "Problem"));
    }

    // --- reportProblemFalse ---

    @Test
    void reportProblemFalse_shouldCallReportProblemContractorFalse_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "No problem";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorFalse(jobId, photo, description))
                .thenReturn(testJobDispatcher);
        jobUserService.reportProblemFalse(testContractor.getId(), jobId, photo, description);
        verify(jobService, times(1)).reportProblemContractorFalse(jobId, photo, description);
    }

    @Test
    void reportProblemFalse_shouldCallReportProblemOwnerFalse_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "No problem";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerFalse(jobId, photo, description))
                .thenReturn(testJobDispatcher);
        jobUserService.reportProblemFalse(testOwner.getId(), jobId, photo, description);
        verify(jobService, times(1)).reportProblemOwnerFalse(jobId, photo, description);
    }

    @Test
    void reportProblemFalse_shouldThrowBusinessException_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () ->
                        jobUserService.reportProblemFalse(
                                unauthorizedUser.getId(), jobId, Optional.empty(), "No problem"));
    }

    // --- endJobSuccessfuly ---

    @Test
    void endJobSuccessfuly_shouldReturnNonNullResponseDto_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Done";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyContractor(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        JobResponseDto result =
                jobUserService.endJobSuccessfuly(testContractor.getId(), jobId, photo, description);
        assertThat(result).isNotNull();
    }

    @Test
    void endJobSuccessfuly_shouldCallEndJobContractor_whenCalledByContractor() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Done";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyContractor(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.endJobSuccessfuly(testContractor.getId(), jobId, photo, description);
        verify(jobService, times(1)).endJobSuccessfulyContractor(jobId, photo, description);
    }

    @Test
    void endJobSuccessfuly_shouldCallEndJobOwner_whenCalledByOwner() {
        UUID jobId = testJob.getId();
        Optional<MultipartFile> photo = Optional.empty();
        String description = "Done";
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyOwner(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);
        jobUserService.endJobSuccessfuly(testOwner.getId(), jobId, photo, description);
        verify(jobService, times(1)).endJobSuccessfulyOwner(jobId, photo, description);
    }

    @Test
    void endJobSuccessfuly_shouldThrowBusinessException_whenCalledByUnauthorizedUser() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUserWithProfilePhoto();
        when(jobService.getJobById(jobId)).thenReturn(testJob);
        assertThrows(
                BusinessException.class,
                () ->
                        jobUserService.endJobSuccessfuly(
                                unauthorizedUser.getId(), jobId, Optional.empty(), "Done"));
    }

    private JobResponseDto createMockJobResponseDto() {
        return org.mockito.Mockito.mock(JobResponseDto.class);
    }

    private Job createJobWithUsers() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        job.setOwner(testOwner);
        job.setContractor(testContractor);
        return job;
    }

    private Offer createOfferWithUsers() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setOwner(testOwner);
        offer.setChosenCandidate(testContractor);
        return offer;
    }
}
