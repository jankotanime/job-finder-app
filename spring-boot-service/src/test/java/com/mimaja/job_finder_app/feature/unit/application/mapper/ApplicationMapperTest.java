package com.mimaja.job_finder_app.feature.unit.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplication;
import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplicationCreateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplicationWithNullCandidate;
import static com.mimaja.job_finder_app.feature.unit.application.mockdata.ApplicationMockData.createTestApplicationWithNullCv;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithProfilePhoto;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapperImpl;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import com.mimaja.job_finder_app.feature.user.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationMapper - Unit Tests")
class ApplicationMapperTest {

    private ApplicationMapper applicationMapper;
    private UserMapper userMapperMock;
    private CvMapper cvMapperMock;

    @BeforeEach
    void setUp() throws Exception {
        setupApplicationMapperWithMocks();
    }

    private void setupApplicationMapperWithMocks() throws Exception {
        userMapperMock = mock(UserMapper.class);
        cvMapperMock = mock(CvMapper.class);

        ApplicationMapperImpl mapperImpl = new ApplicationMapperImpl();
        injectField(mapperImpl, "userMapper", userMapperMock);
        injectField(mapperImpl, "cvMapper", cvMapperMock);
        applicationMapper = mapperImpl;
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should return null when mapping null ApplicationCreateRequestDto")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        // when
        Application result = applicationMapper.toEntity(null);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid ApplicationCreateRequestDto")
    void testToEntity_shouldReturnNonNullApplication_whenValidDtoProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        var dto = createTestApplicationCreateRequestDto(candidate, offer, cv);

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertNotNull(result, "Application should not be null");
    }

    @Test
    @DisplayName("Should map candidate correctly when mapping ApplicationCreateRequestDto")
    void testToEntity_shouldMapCandidateCorrectly_whenValidDtoProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        var dto = createTestApplicationCreateRequestDto(candidate, offer, cv);

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getCandidate()).isEqualTo(candidate);
    }

    @Test
    @DisplayName("Should map offer correctly when mapping ApplicationCreateRequestDto")
    void testToEntity_shouldMapOfferCorrectly_whenValidDtoProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        var dto = createTestApplicationCreateRequestDto(candidate, offer, cv);

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getOffer()).isEqualTo(offer);
    }

    @Test
    @DisplayName("Should map chosenCv correctly when mapping ApplicationCreateRequestDto")
    void testToEntity_shouldMapChosenCvCorrectly_whenValidDtoProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        var dto = createTestApplicationCreateRequestDto(candidate, offer, cv);

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getChosenCv()).isEqualTo(cv);
    }

    @Test
    @DisplayName("Should handle null candidate in ApplicationCreateRequestDto")
    void testToEntity_shouldHandleNullCandidate_whenCandidateIsNull() {
        // given
        var dto = createTestApplicationCreateRequestDto(null, new Offer(), new Cv());

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getCandidate()).isNull();
    }

    @Test
    @DisplayName("Should handle null offer in ApplicationCreateRequestDto")
    void testToEntity_shouldHandleNullOffer_whenOfferIsNull() {
        // given
        var dto = createTestApplicationCreateRequestDto(createTestUser(), null, new Cv());

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getOffer()).isNull();
    }

    @Test
    @DisplayName("Should handle null chosenCv in ApplicationCreateRequestDto")
    void testToEntity_shouldHandleNullChosenCv_whenChosenCvIsNull() {
        // given
        var dto = createTestApplicationCreateRequestDto(createTestUser(), new Offer(), null);

        // when
        Application result = applicationMapper.toEntity(dto);

        // then
        assertThat(result.getChosenCv()).isNull();
    }

    // ==================== toResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Application")
    void testToResponseDto_shouldReturnNull_whenNullApplicationProvided() {
        // given
        Application application = null;

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Application")
    void testToResponseDto_shouldReturnNonNullDto_whenValidApplicationProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertNotNull(result, "ApplicationResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Application")
    void testToResponseDto_shouldMapIdCorrectly_whenValidApplicationProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.id()).isEqualTo(application.getId());
    }

    @Test
    @DisplayName("Should map status correctly when mapping Application")
    void testToResponseDto_shouldMapStatusCorrectly_whenValidApplicationProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.status()).isEqualTo(application.getStatus());
    }

    @Test
    @DisplayName("Should map candidate correctly when mapping Application")
    void testToResponseDto_shouldMapCandidateCorrectly_whenValidApplicationProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);

        // when
        when(userMapperMock.toUserInOfferResponseDto(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                if (user == null) return null;
                return new UserInOfferResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    null
                );
            });
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.candidate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null candidate when mapping Application")
    void testToResponseDto_shouldHandleNullCandidate_whenCandidateIsNull() {
        // given
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplicationWithNullCandidate(offer, cv);

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.candidate()).isNull();
    }

    @Test
    @DisplayName("Should map chosenCv correctly when mapping Application")
    void testToResponseDto_shouldMapChosenCvCorrectly_whenValidApplicationProvided() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Cv cv = new Cv();
        cv.setId(java.util.UUID.randomUUID());
        cv.setStorageKey("test-storage-key");
        Application application = createTestApplication(candidate, offer, cv);
        when(cvMapperMock.toResponseDto(any(Cv.class)))
            .thenReturn(new CvResponseDto(
                cv.getId(),
                cv.getStorageKey()
            ));
        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.chosenCv()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null chosenCv when mapping Application")
    void testToResponseDto_shouldHandleNullChosenCv_whenChosenCvIsNull() {
        // given
        User candidate = createTestUser();
        Offer offer = new Offer();
        Application application = createTestApplicationWithNullCv(candidate, offer);

        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.chosenCv()).isNull();
    }

    @Test
    @DisplayName("Should map candidate with profile photo correctly when mapping Application")
    void testToResponseDto_shouldMapCandidateWithProfilePhoto_whenCandidateHasPhoto() {
        // given
        User candidate = createTestUserWithProfilePhoto();
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);
        when(userMapperMock.toUserInOfferResponseDto(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                if (user == null) return null;
                return new UserInOfferResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    null
                );
            });
        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.candidate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle candidate without profile photo when mapping Application")
    void testToResponseDto_shouldHandleCandidateWithoutProfilePhoto_whenPhotoIsNull() {
        // given
        User candidate = createTestUser();
        candidate.setProfilePhoto(null);
        Offer offer = new Offer();
        Cv cv = new Cv();
        Application application = createTestApplication(candidate, offer, cv);
        when(userMapperMock.toUserInOfferResponseDto(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                if (user == null) return null;
                return new UserInOfferResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    null
                );
            });
        // when
        ApplicationResponseDto result = applicationMapper.toResponseDto(application);

        // then
        assertThat(result.candidate()).isNotNull();
    }
}
