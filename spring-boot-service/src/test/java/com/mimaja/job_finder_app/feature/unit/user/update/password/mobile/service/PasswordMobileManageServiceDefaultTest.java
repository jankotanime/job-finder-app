package com.mimaja.job_finder_app.feature.unit.user.update.password.mobile.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUserWithPasswordHash;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createPrincipal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
@DisplayName("PasswordMobileManageServiceDefault - Unit Tests")
public class PasswordMobileManageServiceDefaultTest {
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String WEAK_PASSWORD = "weak";
    private static final String OLD_HASHED_PASSWORD = "old-hashed-password";
    private static final String NEW_HASHED_PASSWORD = "new-hashed-password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private PasswordManageDataManager passwordManageDataManager;

    private PasswordMobileManageServiceDefault passwordService;
    private User testUser;
    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createDefaultUserWithPasswordHash(OLD_HASHED_PASSWORD);
        testPrincipal = createPrincipal(testUser);
        passwordService = new PasswordMobileManageServiceDefault(
            userRepository,
            passwordConfiguration,
            passwordManageDataManager
        );
    }

    @Test
    @DisplayName("Should update password successfully")
    void shouldUpdatePasswordHash_WhenUpdatePasswordWithValidData() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD))
            .thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePassword(requestDto, testPrincipal);

        assertThat(testUser.getPasswordHash()).isEqualTo(NEW_HASHED_PASSWORD);

        verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should verify old password before updating")
    void shouldVerifyOldPasswordFirst_WhenUpdatingPassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD))
            .thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePassword(requestDto, testPrincipal);

        InOrder inOrder = inOrder(passwordConfiguration, passwordManageDataManager, userRepository);
        inOrder.verify(passwordConfiguration, times(1))
            .verifyPassword(OLD_PASSWORD, OLD_HASHED_PASSWORD);
        inOrder.verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
        inOrder.verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        inOrder.verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when old password is wrong")
    void shouldThrowBusinessException_WhenUpdatePasswordWithWrongOldPassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            WRONG_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> passwordService.updatePassword(requestDto, testPrincipal),
            "Should throw BusinessException when old password is wrong"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate wrong password")
            .isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());

        verify(passwordConfiguration, times(1))
            .verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash());
        verify(passwordManageDataManager, times(0)).checkDataPatterns(anyString());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when new password pattern validation fails")
    void shouldThrowBusinessException_WhenPasswordPatternValidationFailsDuringUpdatePassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            WEAK_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
            .when(passwordManageDataManager)
            .checkDataPatterns(WEAK_PASSWORD);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePassword(requestDto, testPrincipal),
            "Should throw BusinessException when password pattern validation fails"
        );

        verify(passwordConfiguration, times(1))
            .verifyPassword(OLD_PASSWORD, testUser.getPasswordHash());
        verify(passwordManageDataManager, times(1)).checkDataPatterns(WEAK_PASSWORD);
        verify(passwordConfiguration, times(0)).encodePassword(anyString());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void shouldThrowBusinessException_WhenUserSaveFailsDuringUpdatePassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD))
            .thenReturn(NEW_HASHED_PASSWORD);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userRepository)
            .save(testUser);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePassword(requestDto, testPrincipal),
            "Should throw BusinessException when user save fails"
        );

        verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should encode new password before saving user")
    void shouldEncodePasswordBeforeSaving_WhenUpdatingPassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD))
            .thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePassword(requestDto, testPrincipal);

        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        assertThat(testUser.getPasswordHash()).isEqualTo(NEW_HASHED_PASSWORD);
    }

    @Test
    @DisplayName("Should validate data patterns for new password")
    void shouldValidateNewPasswordPatterns_WhenUpdatingPassword() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            OLD_PASSWORD,
            NEW_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(OLD_PASSWORD, testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD))
            .thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePassword(requestDto, testPrincipal);

        verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
    }
}
