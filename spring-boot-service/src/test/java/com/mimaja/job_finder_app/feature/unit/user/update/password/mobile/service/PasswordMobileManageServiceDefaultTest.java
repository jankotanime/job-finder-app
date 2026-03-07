package com.mimaja.job_finder_app.feature.unit.user.update.password.mobile.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
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
@DisplayName("PasswordMobileManageServiceDefault - Unit Tests")
public class PasswordMobileManageServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private PasswordManageDataManager passwordManageDataManager;

    private PasswordMobileManageServiceDefault passwordService;

    private User testUser;

    private JwtPrincipal testPrincipal;

    void setUp() {
        testUser = createTestUser();
        testPrincipal = createTestPrincipal(testUser);

        passwordService = new PasswordMobileManageServiceDefault(
            userRepository,
            passwordConfiguration,
            passwordManageDataManager
        );
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPhoneNumber(123456789);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setProfileDescription("Test profile description");
        user.setPasswordHash("old-hashed-password");
        return user;
    }

    private JwtPrincipal createTestPrincipal(User user) {
        return JwtPrincipal.from(user);
    }

    @Test
    @DisplayName("Should update password successfully")
    void testUpdatePassword_WithValidData_ShouldUpdatePasswordHash() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "newPassword123"
        );

        String newHashedPassword = "new-hashed-password";

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword("newPassword123"))
            .thenReturn(newHashedPassword);

        passwordService.updatePassword(requestDto, testPrincipal);

        assertThat(testUser.getPasswordHash()).isEqualTo(newHashedPassword);

        verify(passwordManageDataManager, times(1)).checkDataPatterns("newPassword123");
        verify(passwordConfiguration, times(1)).encodePassword("newPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should verify old password before updating")
    void testUpdatePassword_ShouldVerifyOldPasswordFirst() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "newPassword123"
        );

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword("newPassword123"))
            .thenReturn("new-hashed-password");

        passwordService.updatePassword(requestDto, testPrincipal);
    }

    @Test
    @DisplayName("Should throw BusinessException when old password is wrong")
    void testUpdatePassword_WithWrongOldPassword_ShouldThrowBusinessException() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "wrongPassword",
            "newPassword123"
        );

        when(passwordConfiguration.verifyPassword("wrongPassword", testUser.getPasswordHash()))
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
            .verifyPassword("wrongPassword", testUser.getPasswordHash());
        verify(passwordManageDataManager, times(0)).checkDataPatterns(anyString());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when new password pattern validation fails")
    void testUpdatePassword_WhenPasswordPatternValidationFails_ShouldThrowBusinessException() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "weak"
        );

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
            .when(passwordManageDataManager)
            .checkDataPatterns("weak");

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePassword(requestDto, testPrincipal),
            "Should throw BusinessException when password pattern validation fails"
        );

        verify(passwordConfiguration, times(1))
            .verifyPassword("oldPassword", testUser.getPasswordHash());
        verify(passwordManageDataManager, times(1)).checkDataPatterns("weak");
        verify(passwordConfiguration, times(0)).encodePassword(anyString());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void testUpdatePassword_WhenUserSaveFails_ShouldThrowBusinessException() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "newPassword123"
        );

        String newHashedPassword = "new-hashed-password";

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword("newPassword123"))
            .thenReturn(newHashedPassword);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userRepository)
            .save(testUser);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePassword(requestDto, testPrincipal),
            "Should throw BusinessException when user save fails"
        );

        verify(passwordManageDataManager, times(1)).checkDataPatterns("newPassword123");
        verify(passwordConfiguration, times(1)).encodePassword("newPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should encode new password before saving user")
    void testUpdatePassword_ShouldEncodePasswordBeforeSaving() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "newPassword123"
        );

        String newHashedPassword = "new-hashed-password";

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword("newPassword123"))
            .thenReturn(newHashedPassword);

        passwordService.updatePassword(requestDto, testPrincipal);

        verify(passwordConfiguration, times(1)).encodePassword("newPassword123");
        assertThat(testUser.getPasswordHash()).isEqualTo(newHashedPassword);
    }

    @Test
    @DisplayName("Should validate data patterns for new password")
    void testUpdatePassword_ShouldValidateNewPasswordPatterns() {
        setUp();

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(
            "oldPassword",
            "newPassword123"
        );

        when(passwordConfiguration.verifyPassword("oldPassword", testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordConfiguration.encodePassword("newPassword123"))
            .thenReturn("new-hashed-password");

        passwordService.updatePassword(requestDto, testPrincipal);

        verify(passwordManageDataManager, times(1)).checkDataPatterns("newPassword123");
    }
}
