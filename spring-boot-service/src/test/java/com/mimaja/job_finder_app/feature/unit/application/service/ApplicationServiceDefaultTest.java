package com.mimaja.job_finder_app.feature.unit.application.service;

import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplication;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithOwner;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationCreateRequestDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.application.model.ApplicationStatus;
import com.mimaja.job_finder_app.feature.application.repository.ApplicationRepository;
import com.mimaja.job_finder_app.feature.application.service.ApplicationServiceDefault;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationServiceDefault - Unit Tests")
public class ApplicationServiceDefaultTest {
    @Mock private ApplicationRepository applicationRepository;

    @Mock private OfferService offerService;

    @Mock private UserService userService;

    @Mock private CvService cvService;

    @Mock private ApplicationMapper applicationMapper;

    private ApplicationServiceDefault applicationService;

    private Application testApplication;
    private User testCandidate;
    private User testOwner;
    private Offer testOffer;
    private Cv testCv;

    @BeforeEach
    void setUp() {
        applicationService =
                new ApplicationServiceDefault(
                        applicationRepository,
                        offerService,
                        userService,
                        cvService,
                        applicationMapper);

        testOwner = createTestUser();
        testCandidate = createTestUser();
        testOffer = createTestOfferWithOwner(testOwner);
        testCv = org.mockito.Mockito.mock(Cv.class);
        testApplication = createTestApplication(testCandidate, testOffer, testCv);
    }

    // ==================== Get All Applications By Offer Id Tests ====================

    @Test
    @DisplayName("Should return applications page for offer")
    void testGetAllApplicationsByOfferId_WithValidOffer_ShouldReturnApplicationsPage() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> expectedPage = new PageImpl<>(List.of(testApplication), pageable, 1);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findAllByOfferId(offerId, pageable)).thenReturn(expectedPage);

        // when
        Page<Application> result =
                applicationService.getAllApplicationsByOfferId(offerId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(testApplication);
    }

    @Test
    @DisplayName("Should call offerService getOfferById before fetching applications")
    void testGetAllApplicationsByOfferId_ShouldVerifyOfferExists() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findAllByOfferId(offerId, pageable)).thenReturn(expectedPage);

        // when
        applicationService.getAllApplicationsByOfferId(offerId, pageable);

        // then
        verify(offerService, times(1)).getOfferById(offerId);
    }

    @Test
    @DisplayName("Should call repository findAllByOfferId with correct parameters")
    void testGetAllApplicationsByOfferId_ShouldCallRepositoryWithCorrectParameters() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findAllByOfferId(offerId, pageable)).thenReturn(expectedPage);

        // when
        applicationService.getAllApplicationsByOfferId(offerId, pageable);

        // then
        verify(applicationRepository, times(1)).findAllByOfferId(offerId, pageable);
    }

    // ==================== Get Application By Id Tests ====================

    @Test
    @DisplayName("Should return application when application exists")
    void testGetApplicationById_WithExistingApplication_ShouldReturnApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));

        // when
        Application result = applicationService.getApplicationById(offerId, applicationId);

        // then
        assertNotNull(result, "Application should not be null");
        assertThat(result.getId()).isEqualTo(applicationId);
    }

    @Test
    @DisplayName("Should throw BusinessException when application not found")
    void testGetApplicationById_WithNonExistentApplication_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = UUID.randomUUID();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> applicationService.getApplicationById(offerId, applicationId),
                        "Should throw BusinessException when application not found");

        assertThat(exception.getCode())
                .as("Exception code should indicate application not found")
                .isEqualTo(BusinessExceptionReason.APPLICATION_NOT_FOUND.getCode());
    }

    // ==================== Accept Application Tests ====================

    @Test
    @DisplayName("Should accept application and update offer")
    void testAcceptApplication_WithValidApplication_ShouldAcceptAndUpdateOffer() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testOffer.setStatus(OfferStatus.OPEN);
        testApplication.setStatus(ApplicationStatus.SENT);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        Application result = applicationService.acceptApplication(offerId, applicationId);

        // then
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should set offer status to CLOSED when accepting application")
    void testAcceptApplication_ShouldSetOfferStatusToClosed() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testOffer.setStatus(OfferStatus.OPEN);
        testApplication.setStatus(ApplicationStatus.SENT);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.acceptApplication(offerId, applicationId);

        // then
        assertThat(testOffer.getStatus()).isEqualTo(OfferStatus.CLOSED);
    }

    @Test
    @DisplayName("Should set chosen candidate when accepting application")
    void testAcceptApplication_ShouldSetChosenCandidate() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testOffer.setStatus(OfferStatus.OPEN);
        testApplication.setStatus(ApplicationStatus.SENT);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.acceptApplication(offerId, applicationId);

        // then
        assertThat(testOffer.getChosenCandidate()).isEqualTo(testCandidate);
    }

    @Test
    @DisplayName("Should throw BusinessException when application already reviewed")
    void testAcceptApplication_WithAlreadyReviewedApplication_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testApplication.setStatus(ApplicationStatus.ACCEPTED);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> applicationService.acceptApplication(offerId, applicationId),
                        "Should throw BusinessException when application already reviewed");

        assertThat(exception.getCode())
                .as("Exception code should indicate application already reviewed")
                .isEqualTo(BusinessExceptionReason.APPLICATION_ALREADY_REVIEWED.getCode());
    }

    // ==================== Reject Application Tests ====================

    @Test
    @DisplayName("Should reject application")
    void testRejectApplication_WithValidApplication_ShouldRejectApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testApplication.setStatus(ApplicationStatus.SENT);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        Application result = applicationService.rejectApplication(offerId, applicationId);

        // then
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    @DisplayName("Should call repository save when rejecting application")
    void testRejectApplication_ShouldSaveApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testApplication.setStatus(ApplicationStatus.SENT);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.rejectApplication(offerId, applicationId);

        // then
        verify(applicationRepository, times(1)).save(testApplication);
    }

    @Test
    @DisplayName("Should throw BusinessException when rejecting already reviewed application")
    void testRejectApplication_WithAlreadyReviewedApplication_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        testApplication.setStatus(ApplicationStatus.REJECTED);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(testApplication));

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> applicationService.rejectApplication(offerId, applicationId),
                        "Should throw BusinessException when application already reviewed");

        assertThat(exception.getCode())
                .as("Exception code should indicate application already reviewed")
                .isEqualTo(BusinessExceptionReason.APPLICATION_ALREADY_REVIEWED.getCode());
    }

    // ==================== Send Application Tests ====================

    @Test
    @DisplayName("Should send application successfully")
    void testSendApplication_WithValidData_ShouldReturnApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = testCv.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        testOffer.setStatus(OfferStatus.OPEN);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(applicationMapper.toEntity(any(ApplicationCreateRequestDto.class)))
                .thenReturn(testApplication);
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        Application result = applicationService.sendApplication(offerId, userId, requestDto);

        // then
        assertNotNull(result, "Application should not be null");
    }

    @Test
    @DisplayName("Should verify offer is open before sending application")
    void testSendApplication_ShouldVerifyOfferIsOpen() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = testCv.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        testOffer.setStatus(OfferStatus.CLOSED);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> applicationService.sendApplication(offerId, userId, requestDto),
                        "Should throw BusinessException when offer is closed");

        assertThat(exception.getCode())
                .as("Exception code should indicate offer candidates limit")
                .isEqualTo(BusinessExceptionReason.OFFER_CANDIDATES_LIMIT.getCode());
    }

    @Test
    @DisplayName("Should get CV before sending application")
    void testSendApplication_ShouldGetCvById() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = testCv.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        testOffer.setStatus(OfferStatus.OPEN);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(applicationMapper.toEntity(any(ApplicationCreateRequestDto.class)))
                .thenReturn(testApplication);
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.sendApplication(offerId, userId, requestDto);

        // then
        verify(cvService, times(1)).getCvById(cvId);
    }

    @Test
    @DisplayName("Should get user before sending application")
    void testSendApplication_ShouldGetUserById() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = testCv.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        testOffer.setStatus(OfferStatus.OPEN);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(applicationMapper.toEntity(any(ApplicationCreateRequestDto.class)))
                .thenReturn(testApplication);
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.sendApplication(offerId, userId, requestDto);

        // then
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("Should save application after creating")
    void testSendApplication_ShouldSaveApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = testCv.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        testOffer.setStatus(OfferStatus.OPEN);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(applicationMapper.toEntity(any(ApplicationCreateRequestDto.class)))
                .thenReturn(testApplication);
        when(applicationRepository.save(testApplication)).thenReturn(testApplication);

        // when
        applicationService.sendApplication(offerId, userId, requestDto);

        // then
        verify(applicationRepository, times(1)).save(testApplication);
    }
}
