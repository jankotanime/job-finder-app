package com.mimaja.job_finder_app.feature.unit.user.update.password.mobile.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_NEW_HASHED_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_NEW_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_OLD_HASHED_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_OLD_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithPassword;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.password.mobile.service.PasswordMobileManageServiceDefault;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordManageDataManager;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordRequestDto;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
class PasswordMobileManageServiceDefaultTest {

    private static final String TEST_WRONG_PASSWORD = "wrongPassword";

    @Mock private UserRepository userRepository;
    @Mock private PasswordConfiguration passwordConfiguration;
    @Mock private PasswordManageDataManager passwordManageDataManager;

    private PasswordMobileManageServiceDefault passwordService;
    private User testUser;
    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createTestUserWithPassword();
        testPrincipal = JwtPrincipal.from(testUser);
        passwordService = new PasswordMobileManageServiceDefault(
                userRepository, passwordConfiguration, passwordManageDataManager);
    }

    @Test
    void updatePassword_shouldUpdatePasswordHash_whenDataIsValid() {
        setupValidPasswordMocks();
        passwordService.updatePassword(createValidRequest(), testPrincipal);
        assertThat(testUser.getPasswordHash()).isEqualTo(TEST_NEW_HASHED_PASSWORD);
    }

    @Test
    void updatePassword_shouldCheckDataPatterns_whenUpdatingPassword() {
        setupValidPasswordMocks();
        passwordService.updatePassword(createValidRequest(), testPrincipal);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(TEST_NEW_PASSWORD);
    }

    @Test
    void updatePassword_shouldEncodeNewPassword_whenUpdatingPassword() {
        setupValidPasswordMocks();
        passwordService.updatePassword(createValidRequest(), testPrincipal);
        verify(passwordConfiguration, times(1)).encodePassword(TEST_NEW_PASSWORD);
    }

    @Test
    void updatePassword_shouldSaveUser_whenUpdatingPassword() {
        setupValidPasswordMocks();
        passwordService.updatePassword(createValidRequest(), testPrincipal);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePassword_shouldThrowExceptionWithWrongPasswordCode_whenOldPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_OLD_HASHED_PASSWORD))
                .thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createRequestWithOldPassword(TEST_WRONG_PASSWORD), testPrincipal));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());
    }

    @Test
    void updatePassword_shouldNotCheckPatterns_whenOldPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_OLD_HASHED_PASSWORD))
                .thenReturn(false);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createRequestWithOldPassword(TEST_WRONG_PASSWORD), testPrincipal));
        verify(passwordManageDataManager, never()).checkDataPatterns(anyString());
    }

    @Test
    void updatePassword_shouldNotSaveUser_whenOldPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_OLD_HASHED_PASSWORD))
                .thenReturn(false);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createRequestWithOldPassword(TEST_WRONG_PASSWORD), testPrincipal));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_shouldThrowBusinessException_whenPatternValidationFails() {
        when(passwordConfiguration.verifyPassword(TEST_OLD_PASSWORD, TEST_OLD_HASHED_PASSWORD)).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
                .when(passwordManageDataManager).checkDataPatterns("weak");
        assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createRequestWithNewPassword("weak"), testPrincipal));
    }

    @Test
    void updatePassword_shouldNotEncodePassword_whenPatternValidationFails() {
        when(passwordConfiguration.verifyPassword(TEST_OLD_PASSWORD, TEST_OLD_HASHED_PASSWORD)).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
                .when(passwordManageDataManager).checkDataPatterns("weak");
        assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createRequestWithNewPassword("weak"), testPrincipal));
        verify(passwordConfiguration, never()).encodePassword(anyString());
    }

    @Test
    void updatePassword_shouldThrowBusinessException_whenSaveFails() {
        setupValidPasswordMocks();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userRepository).save(testUser);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePassword(createValidRequest(), testPrincipal));
    }

    private void setupValidPasswordMocks() {
        when(passwordConfiguration.verifyPassword(TEST_OLD_PASSWORD, TEST_OLD_HASHED_PASSWORD)).thenReturn(true);
        when(passwordConfiguration.encodePassword(TEST_NEW_PASSWORD)).thenReturn(TEST_NEW_HASHED_PASSWORD);
    }

    private UpdatePasswordRequestDto createValidRequest() {
        return new UpdatePasswordRequestDto(TEST_OLD_PASSWORD, TEST_NEW_PASSWORD);
    }

    private UpdatePasswordRequestDto createRequestWithOldPassword(String oldPassword) {
        return new UpdatePasswordRequestDto(oldPassword, TEST_NEW_PASSWORD);
    }

    private UpdatePasswordRequestDto createRequestWithNewPassword(String newPassword) {
        return new UpdatePasswordRequestDto(TEST_OLD_PASSWORD, newPassword);
    }
}
