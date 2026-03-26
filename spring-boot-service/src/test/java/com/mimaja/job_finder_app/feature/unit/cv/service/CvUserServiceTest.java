package com.mimaja.job_finder_app.feature.unit.cv.service;

import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCv;
import static com.mimaja.job_finder_app.feature.unit.security.mockdata.SecurityMockData.createTestJwtPrincipal;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.cv.service.CvUserService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CvUserServiceTest {
    @Mock private CvService cvService;
    @Mock private UserService userService;
    @Mock private CvMapper cvMapper;
    @InjectMocks private CvUserService cvUserService;

    private Cv testCv;
    private User testUser;
    private JwtPrincipal testJwt;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testCv = createTestCv();
        testCv.setUser(testUser);
        testJwt = createTestJwtPrincipal(testUser);
    }

    // --- uploadCv ---

    @Test
    void uploadCv_shouldReturnNonNullResponse_whenUploadSucceeds() {
        // given
        when(cvService.uploadCv(any(), any())).thenReturn(testCv);
        when(cvMapper.toResponseDto(testCv)).thenReturn(createTestCvResponseDto());

        // when
        CvResponseDto result = cvUserService.uploadCv(createMockFile(), testJwt);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void uploadCv_shouldCallCvServiceUploadCv_whenInvoked() {
        // given
        when(cvService.uploadCv(any(), any())).thenReturn(testCv);
        when(cvMapper.toResponseDto(any())).thenReturn(createTestCvResponseDto());

        // when
        cvUserService.uploadCv(createMockFile(), testJwt);

        // then
        verify(cvService, times(1)).uploadCv(any(), any());
    }

    // --- getCvById ---

    @Test
    void getCvById_shouldReturnNonNullResponse_whenCvExists() {
        // given
        UUID cvId = testCv.getId();
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(cvMapper.toResponseDto(testCv)).thenReturn(createTestCvResponseDto());

        // when
        CvResponseDto result = cvUserService.getCvById(cvId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getCvById_shouldCallCvServiceGetCvById_whenInvoked() {
        // given
        UUID cvId = testCv.getId();
        when(cvService.getCvById(cvId)).thenReturn(testCv);
        when(cvMapper.toResponseDto(any())).thenReturn(createTestCvResponseDto());

        // when
        cvUserService.getCvById(cvId);

        // then
        verify(cvService, times(1)).getCvById(cvId);
    }

    // --- getCvsByUserId ---

    @Test
    void getCvsByUserId_shouldReturnNonEmptyList_whenUserHasCvs() {
        // given
        UUID userId = testJwt.id();
        when(cvService.getCvsByUserId(userId)).thenReturn(List.of(testCv));
        when(cvMapper.toResponseDto(testCv)).thenReturn(createTestCvResponseDto());

        // when
        List<CvResponseDto> result = cvUserService.getCvsByUserId(userId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void getCvsByUserId_shouldReturnEmptyList_whenUserHasNoCvs() {
        // given
        UUID userId = testJwt.id();
        when(cvService.getCvsByUserId(userId)).thenReturn(List.of());

        // when
        List<CvResponseDto> result = cvUserService.getCvsByUserId(userId);

        // then
        assertThat(result).isEmpty();
    }

    // --- updateCv ---

    @Test
    void updateCv_shouldReturnNonNullResponse_whenUserIsOwner() {
        // given
        UUID cvId = testCv.getId();
        setupOwnershipMocks(cvId);
        when(cvService.updateCv(any(), any())).thenReturn(testCv);
        when(cvMapper.toResponseDto(testCv)).thenReturn(createTestCvResponseDto());

        // when
        CvResponseDto result = cvUserService.updateCv(createMockFile(), testJwt, cvId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateCv_shouldThrowBusinessException_whenUserIsNotOwner() {
        // given
        UUID cvId = testCv.getId();
        User differentUser = createTestUser();
        JwtPrincipal differentJwt = createTestJwtPrincipal(differentUser);
        when(userService.getUserById(differentUser.getId())).thenReturn(differentUser);
        when(cvService.getCvById(cvId)).thenReturn(testCv);

        // when / then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> cvUserService.updateCv(createMockFile(), differentJwt, cvId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    // --- deleteCv ---

    @Test
    void deleteCv_shouldCallCvServiceDeleteCv_whenUserIsOwner() {
        // given
        UUID cvId = testCv.getId();
        setupOwnershipMocks(cvId);

        // when
        cvUserService.deleteCv(testJwt, cvId);

        // then
        verify(cvService, times(1)).deleteCv(cvId);
    }

    @Test
    void deleteCv_shouldThrowBusinessException_whenUserIsNotOwner() {
        // given
        UUID cvId = testCv.getId();
        User differentUser = createTestUser();
        JwtPrincipal differentJwt = createTestJwtPrincipal(differentUser);
        when(userService.getUserById(differentUser.getId())).thenReturn(differentUser);
        when(cvService.getCvById(cvId)).thenReturn(testCv);

        // when / then
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> cvUserService.deleteCv(differentJwt, cvId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    // --- deleteAllCvsForUser ---

    @Test
    void deleteAllCvsForUser_shouldCallCvServiceDeleteAll_whenInvoked() {
        // when
        cvUserService.deleteAllCvsForUser(testJwt);

        // then
        verify(cvService, times(1)).deleteAllCvsForUser(testJwt.id());
    }

    private void setupOwnershipMocks(UUID cvId) {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(cvService.getCvById(cvId)).thenReturn(testCv);
    }

    private MultipartFile createMockFile() {
        return mock(MultipartFile.class);
    }

    private CvResponseDto createTestCvResponseDto() {
        return mock(CvResponseDto.class);
    }
}
