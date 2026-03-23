package com.mimaja.job_finder_app.feature.unit.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithOwner;
import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplication;

import java.util.List;
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

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.application.service.ApplicationService;
import com.mimaja.job_finder_app.feature.application.service.ApplicationServiceUser;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationServiceUser - Unit Tests")
public class ApplicationServiceUserTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private OfferService offerService;

    private ApplicationServiceUser applicationServiceUser;

    private Application testApplication;
    private User testCandidate;
    private User testOwner;
    private User testDifferentUser;
    private Offer testOffer;
    private Cv testCv;
    private JwtPrincipal ownerJwt;
    private JwtPrincipal candidateJwt;
    private JwtPrincipal differentUserJwt;

    @BeforeEach
    void setUp() {
        applicationServiceUser = new ApplicationServiceUser(applicationService, applicationMapper, offerService);

        testOwner = createTestUser();
        testCandidate = createTestUser();
        testDifferentUser = createTestUser();
        testOffer = createTestOfferWithOwner(testOwner);
        testCv = org.mockito.Mockito.mock(Cv.class);
        testApplication = createTestApplication(testCandidate, testOffer, testCv);

        ownerJwt = createJwtPrincipal(testOwner);
        candidateJwt = createJwtPrincipal(testCandidate);
        differentUserJwt = createJwtPrincipal(testDifferentUser);
    }

    // ==================== Get All Applications By Offer Id Tests ====================

    @Test
    @DisplayName("Should return applications page when user is offer owner")
    void testGetAllApplicationsByOfferId_WithUserIsOwner_ShouldReturnApplicationsPage() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> applicationsPage = new PageImpl<>(List.of(testApplication), pageable, 1);
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.getAllApplicationsByOfferId(offerId, pageable)).thenReturn(applicationsPage);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        Page<ApplicationResponseDto> result = applicationServiceUser.getAllApplicationsByOfferId(offerId, ownerJwt, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(responseDto);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not offer owner")
    void testGetAllApplicationsByOfferId_WithUserIsNotOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.getAllApplicationsByOfferId(offerId, differentUserJwt, pageable),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    @Test
    @DisplayName("Should verify ownership before fetching applications")
    void testGetAllApplicationsByOfferId_ShouldVerifyOwnership() {
        // given
        UUID offerId = testOffer.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.getAllApplicationsByOfferId(offerId, pageable)).thenReturn(emptyPage);

        // when
        applicationServiceUser.getAllApplicationsByOfferId(offerId, ownerJwt, pageable);

        // then
        verify(offerService, times(1)).getOfferById(offerId);
    }

    // ==================== Get Application By Id Tests ====================

    @Test
    @DisplayName("Should return application when user is offer owner")
    void testGetApplicationById_WithUserIsOfferOwner_ShouldReturnApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(applicationService.getApplicationById(offerId, applicationId)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.getApplicationById(offerId, applicationId, ownerJwt);

        // then
        assertNotNull(result, "Application response should not be null");
    }

    @Test
    @DisplayName("Should return application when user is application candidate")
    void testGetApplicationById_WithUserIsCandidate_ShouldReturnApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(applicationService.getApplicationById(offerId, applicationId)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.getApplicationById(offerId, applicationId, candidateJwt);

        // then
        assertNotNull(result, "Application response should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when user is neither owner nor candidate")
    void testGetApplicationById_WithUserIsNeither_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();

        when(applicationService.getApplicationById(offerId, applicationId)).thenReturn(testApplication);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.getApplicationById(offerId, applicationId, differentUserJwt),
            "Should throw BusinessException when user is not owner or candidate"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    // ==================== Send Application Tests ====================

    @Test
    @DisplayName("Should send application successfully")
    void testSendApplication_WithValidCandidate_ShouldReturnApplication() {
        // given
        UUID offerId = testOffer.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(testCv.getId());
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.sendApplication(offerId, candidateJwt.id(), requestDto)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.sendApplication(offerId, candidateJwt, requestDto);

        // then
        assertNotNull(result, "Application response should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when user is offer owner")
    void testSendApplication_WithOfferOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(testCv.getId());

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.sendApplication(offerId, ownerJwt, requestDto),
            "Should throw BusinessException when owner tries to apply"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate owner cannot apply")
            .isEqualTo(BusinessExceptionReason.OWNER_CANNOT_APPLY.getCode());
    }

    @Test
    @DisplayName("Should throw BusinessException when candidate already applied")
    void testSendApplication_WithAlreadyAppliedCandidate_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(testCv.getId());
        testOffer.addApplication(testApplication);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.sendApplication(offerId, candidateJwt, requestDto),
            "Should throw BusinessException when candidate already applied"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate already applied")
            .isEqualTo(BusinessExceptionReason.ALREADY_APPLIED_FOR_OFFER.getCode());
    }

    @Test
    @DisplayName("Should allow application when offer has applications from other candidates")
    void testSendApplication_WithApplicationsFromOtherCandidates_ShouldAllowApplication() {
        // given
        UUID offerId = testOffer.getId();
        User otherCandidate = createTestUser();
        Application otherApplication = createTestApplication(otherCandidate, testOffer, testCv);
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(testCv.getId());
        testOffer.addApplication(otherApplication);

        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.sendApplication(offerId, candidateJwt.id(), requestDto)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.sendApplication(offerId, candidateJwt, requestDto);

        // then
        assertNotNull(result, "Application response should not be null");
        verify(applicationService, times(1)).sendApplication(offerId, candidateJwt.id(), requestDto);
    }

    // ==================== Accept Application Tests ====================

    @Test
    @DisplayName("Should accept application when user is offer owner")
    void testAcceptApplication_WithOfferOwner_ShouldAcceptApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.acceptApplication(offerId, applicationId)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.acceptApplication(offerId, applicationId, ownerJwt);

        // then
        assertNotNull(result, "Application response should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not offer owner during accept")
    void testAcceptApplication_WithNonOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.acceptApplication(offerId, applicationId, candidateJwt),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    // ==================== Reject Application Tests ====================

    @Test
    @DisplayName("Should reject application when user is offer owner")
    void testRejectApplication_WithOfferOwner_ShouldRejectApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();
        ApplicationResponseDto responseDto = org.mockito.Mockito.mock(ApplicationResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(applicationService.rejectApplication(offerId, applicationId)).thenReturn(testApplication);
        when(applicationMapper.toResponseDto(testApplication)).thenReturn(responseDto);

        // when
        ApplicationResponseDto result = applicationServiceUser.rejectApplication(offerId, applicationId, ownerJwt);

        // then
        assertNotNull(result, "Application response should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not offer owner during reject")
    void testRejectApplication_WithNonOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID applicationId = testApplication.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> applicationServiceUser.rejectApplication(offerId, applicationId, candidateJwt),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    // ==================== Helper Methods ====================

    private JwtPrincipal createJwtPrincipal(User user) {
        return JwtPrincipal.from(user);
    }
}
