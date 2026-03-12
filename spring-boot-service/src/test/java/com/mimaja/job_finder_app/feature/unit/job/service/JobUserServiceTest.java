package com.mimaja.job_finder_app.feature.unit.job.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.dto.JobDispatcherResponseDto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.service.JobService;
import com.mimaja.job_finder_app.feature.job.service.JobUserService;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobUserService - Unit Tests")
public class JobUserServiceTest {
    @Mock
    private JobService jobService;

    @Mock
    private JobMapper jobMapper;

    @Mock
    private OfferService offerService;

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
        testOwner = createTestUser();
        testContractor = createTestUser();
        testJob = createTestJob();
        testOffer = createTestOffer();
        testJobResponseDto = org.mockito.Mockito.mock(JobResponseDto.class);
        testJobDispatcher = createTestJobDispatcher();
        testJwtPrincipal = JwtPrincipal.from(testOwner);

        jobUserService = new JobUserService(
            jobService,
            jobMapper,
            offerService
        );
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        ProfilePhotoCreateRequestDto profilePhotoCreateRequestDto = new ProfilePhotoCreateRequestDto(
          "example",
          MimeType.JPG,
          0,
          "example"
        );
        user.setProfilePhoto(ProfilePhoto.from(profilePhotoCreateRequestDto));
        return user;
    }

    private Job createTestJob() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        job.setOwner(testOwner);
        job.setContractor(testContractor);
        return job;
    }

    private Offer createTestOffer() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setOwner(testOwner);
        offer.setChosenCandidate(testContractor);
        return offer;
    }

    private JobDispatcher createTestJobDispatcher() {
        JobDispatcher jobDispatcher = new JobDispatcher();
        jobDispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        jobDispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        jobDispatcher.setContractiorApprovals(new HashSet<>());
        return jobDispatcher;
    }

    @Test
    @DisplayName("Should get job by id successfully for owner")
    void testGetJobById_WithValidJobIdAndOwner_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        testJob.setOwner(testJwtPrincipal.user());
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.getJobById(testJwtPrincipal, jobId);

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(2)).getJobById(jobId);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should get job by id successfully for contractor")
    void testGetJobById_WithValidJobIdAndContractor_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        testJob.setContractor(testContractor);
        JwtPrincipal contractorPrincipal = JwtPrincipal.from(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.getJobById(contractorPrincipal, jobId);

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(2)).getJobById(jobId);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should throw BusinessException when getting job as unauthorized user")
    void testGetJobById_WithUnauthorizedUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUser();
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(unauthorizedUser);

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.getJobById(unauthorizedPrincipal, jobId),
            "Should throw BusinessException for unauthorized user"
        );
    }

    @Test
    @DisplayName("Should get jobs as contractor successfully")
    void testGetJobsAsContractor_WithValidUserId_ShouldReturnJobResponseDtoList() {
        List<Job> jobs = List.of(testJob);

        when(jobService.getJobsAsContractor(testJwtPrincipal.id())).thenReturn(jobs);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        List<JobResponseDto> result = jobUserService.getJobsAsContractor(testJwtPrincipal);

        assertNotNull(result, "Jobs list should not be null");
        assertThat(result).hasSize(1);
        verify(jobService, times(1)).getJobsAsContractor(testJwtPrincipal.id());
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should return empty list when contractor has no jobs")
    void testGetJobsAsContractor_WithNoJobs_ShouldReturnEmptyList() {
        when(jobService.getJobsAsContractor(testJwtPrincipal.id())).thenReturn(List.of());

        List<JobResponseDto> result = jobUserService.getJobsAsContractor(testJwtPrincipal);

        assertThat(result).isEmpty();
        verify(jobService, times(1)).getJobsAsContractor(testJwtPrincipal.id());
    }

    @Test
    @DisplayName("Should get jobs as owner successfully")
    void testGetJobsAsOwner_WithValidUserId_ShouldReturnJobResponseDtoList() {
        List<Job> jobs = List.of(testJob);

        when(jobService.getJobsAsOwner(testJwtPrincipal.id())).thenReturn(jobs);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        List<JobResponseDto> result = jobUserService.getJobsAsOwner(testJwtPrincipal);

        assertNotNull(result, "Jobs list should not be null");
        assertThat(result).hasSize(1);
        verify(jobService, times(1)).getJobsAsOwner(testJwtPrincipal.id());
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should return empty list when owner has no jobs")
    void testGetJobsAsOwner_WithNoJobs_ShouldReturnEmptyList() {
        when(jobService.getJobsAsOwner(testJwtPrincipal.id())).thenReturn(List.of());

        List<JobResponseDto> result = jobUserService.getJobsAsOwner(testJwtPrincipal);

        assertThat(result).isEmpty();
        verify(jobService, times(1)).getJobsAsOwner(testJwtPrincipal.id());
    }

    @Test
    @DisplayName("Should create job successfully")
    void testCreateJob_WithValidOfferAndOwner_ShouldReturnJobResponseDto() {
        UUID offerId = testOffer.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(jobService.createJob(testOffer)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.createJob(testJwtPrincipal, offerId);

        assertNotNull(result, "JobResponseDto should not be null");
        verify(offerService, times(2)).getOfferById(offerId);
        verify(jobService, times(1)).createJob(testOffer);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should throw BusinessException when creating job as non-owner")
    void testCreateJob_WithNonOwnerUser_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        User unauthorizedUser = createTestUser();
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(unauthorizedUser);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.createJob(unauthorizedPrincipal, offerId),
            "Should throw BusinessException for non-owner user"
        );

        verify(offerService, times(2)).getOfferById(offerId);
    }

    @Test
    @DisplayName("Should delete job successfully")
    void testDeleteJob_WithValidJobIdAndOwner_ShouldDeleteJob() {
        UUID jobId = testJob.getId();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        jobUserService.deleteJob(testJwtPrincipal, jobId);

        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).deleteJob(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting job as non-owner")
    void testDeleteJob_WithNonOwnerUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUser();
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(unauthorizedUser);

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.deleteJob(unauthorizedPrincipal, jobId),
            "Should throw BusinessException for non-owner user"
        );

        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(0)).deleteJob(jobId);
    }

    @Test
    @DisplayName("Should start job successfully")
    void testStartJob_WithValidJobIdAndOwner_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.startJob(jobId)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.startJob(testOwner.getId(), jobId);

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).startJob(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when starting job as non-owner")
    void testStartJob_WithNonOwnerUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUser();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.startJob(unauthorizedUser.getId(), jobId),
            "Should throw BusinessException for non-owner user"
        );

        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(0)).startJob(jobId);
    }

    @Test
    @DisplayName("Should get job dispatcher successfully for owner")
    void testGetJobDispatcher_WithValidJobIdAndOwner_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.getJobDispatcherByJobId(jobId)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.getJobDispatcher(testOwner.getId(), jobId);

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).getJobDispatcherByJobId(jobId);
    }

    @Test
    @DisplayName("Should get job dispatcher successfully for contractor")
    void testGetJobDispatcher_WithValidJobIdAndContractor_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.getJobDispatcherByJobId(jobId)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.getJobDispatcher(testContractor.getId(), jobId);

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).getJobDispatcherByJobId(jobId);
    }

    @Test
    @DisplayName("Should throw BusinessException when getting job dispatcher as unauthorized user")
    void testGetJobDispatcher_WithUnauthorizedUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        User unauthorizedUser = createTestUser();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.getJobDispatcher(unauthorizedUser.getId(), jobId),
            "Should throw BusinessException for unauthorized user"
        );

        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(0)).getJobDispatcherByJobId(jobId);
    }

    @Test
    @DisplayName("Should report problem true as contractor successfully")
    void testReportProblemTrue_AsContractorWithValidData_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test problem description";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorTrue(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemTrue(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemContractorTrue(jobId, photo, description);
    }

    @Test
    @DisplayName("Should report problem true as owner successfully")
    void testReportProblemTrue_AsOwnerWithValidData_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test problem description";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerTrue(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemTrue(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemOwnerTrue(jobId, photo, description);
    }

    @Test
    @DisplayName("Should throw BusinessException when reporting problem true as unauthorized user")
    void testReportProblemTrue_WithUnauthorizedUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        String description = "Test problem description";
        Optional<MultipartFile> photo = Optional.empty();
        User unauthorizedUser = createTestUser();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.reportProblemTrue(unauthorizedUser.getId(), jobId, photo, description),
            "Should throw BusinessException for unauthorized user"
        );

        verify(jobService, times(1)).getJobById(jobId);
    }

    @Test
    @DisplayName("Should report problem false as contractor successfully")
    void testReportProblemFalse_AsContractorWithValidData_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test no problem description";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorFalse(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemFalse(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemContractorFalse(jobId, photo, description);
    }

    @Test
    @DisplayName("Should report problem false as owner successfully")
    void testReportProblemFalse_AsOwnerWithValidData_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test no problem description";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerFalse(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemFalse(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemOwnerFalse(jobId, photo, description);
    }

    @Test
    @DisplayName("Should throw BusinessException when reporting problem false as unauthorized user")
    void testReportProblemFalse_WithUnauthorizedUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        String description = "Test no problem description";
        Optional<MultipartFile> photo = Optional.empty();
        User unauthorizedUser = createTestUser();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.reportProblemFalse(unauthorizedUser.getId(), jobId, photo, description),
            "Should throw BusinessException for unauthorized user"
        );

        verify(jobService, times(1)).getJobById(jobId);
    }

    @Test
    @DisplayName("Should end job successfully as contractor")
    void testEndJobSuccessfuly_AsContractorWithValidData_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Job finished successfully";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyContractor(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.endJobSuccessfuly(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).endJobSuccessfulyContractor(jobId, photo, description);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should end job successfully as owner")
    void testEndJobSuccessfuly_AsOwnerWithValidData_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Job finished successfully";
        Optional<MultipartFile> photo = Optional.empty();
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyOwner(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.endJobSuccessfuly(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).endJobSuccessfulyOwner(jobId, photo, description);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should throw BusinessException when ending job as unauthorized user")
    void testEndJobSuccessfuly_WithUnauthorizedUser_ShouldThrowBusinessException() {
        UUID jobId = testJob.getId();
        String description = "Job finished successfully";
        Optional<MultipartFile> photo = Optional.empty();
        User unauthorizedUser = createTestUser();

        when(jobService.getJobById(jobId)).thenReturn(testJob);

        assertThrows(
            BusinessException.class,
            () -> jobUserService.endJobSuccessfuly(unauthorizedUser.getId(), jobId, photo, description),
            "Should throw BusinessException for unauthorized user"
        );

        verify(jobService, times(1)).getJobById(jobId);
    }

    @Test
    @DisplayName("Should report problem true as contractor with photo successfully")
    void testReportProblemTrue_AsContractorWithPhoto_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test problem with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorTrue(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemTrue(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemContractorTrue(jobId, photo, description);
    }

    @Test
    @DisplayName("Should report problem false as owner with photo successfully")
    void testReportProblemFalse_AsOwnerWithPhoto_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test no problem with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerFalse(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemFalse(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemOwnerFalse(jobId, photo, description);
    }

    @Test
    @DisplayName("Should end job successfully as owner with photo")
    void testEndJobSuccessfuly_AsOwnerWithPhoto_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Job finished with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyOwner(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.endJobSuccessfuly(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).endJobSuccessfulyOwner(jobId, photo, description);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should end job successfully as contractor with photo")
    void testEndJobSuccessfuly_AsContractorWithPhoto_ShouldReturnJobResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Job finished with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.endJobSuccessfulyContractor(jobId, photo, description)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(testJobResponseDto);

        JobResponseDto result = jobUserService.endJobSuccessfuly(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).endJobSuccessfulyContractor(jobId, photo, description);
        verify(jobMapper, times(1)).toResponseDto(testJob);
    }

    @Test
    @DisplayName("Should report problem false as contractor with photo successfully")
    void testReportProblemFalse_AsContractorWithPhoto_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test no problem with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setContractor(testContractor);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemContractorFalse(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemFalse(
            testContractor.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemContractorFalse(jobId, photo, description);
    }

    @Test
    @DisplayName("Should report problem true as owner with photo successfully")
    void testReportProblemTrue_AsOwnerWithPhoto_ShouldReturnJobDispatcherResponseDto() {
        UUID jobId = testJob.getId();
        String description = "Test problem with photo";
        MultipartFile mockFile = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photo = Optional.of(mockFile);
        testJob.setOwner(testOwner);

        when(jobService.getJobById(jobId)).thenReturn(testJob);
        when(jobService.reportProblemOwnerTrue(jobId, photo, description)).thenReturn(testJobDispatcher);

        JobDispatcherResponseDto result = jobUserService.reportProblemTrue(
            testOwner.getId(),
            jobId,
            photo,
            description
        );

        assertNotNull(result, "JobDispatcherResponseDto should not be null");
        verify(jobService, times(1)).getJobById(jobId);
        verify(jobService, times(1)).reportProblemOwnerTrue(jobId, photo, description);
    }
}
