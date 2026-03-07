package com.mimaja.job_finder_app.feature.unit.user.update.password.website.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordManageDataManager;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordWebsiteManager;
import com.mimaja.job_finder_app.feature.user.update.password.website.service.PasswordWebsiteManageServiceDefault;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.SendEmailToUpdatePasswordRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordByEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.SendEmailToUpdatePasswordResponseDto;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenServiceDefault;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordWebsiteManageServiceDefault - Unit Tests")
public class PasswordWebsiteManageServiceDefaultTest {

    @Mock
    private PasswordWebsiteManager passwordWebsiteManager;

    @Mock
    private ResetTokenServiceDefault resetTokenServiceDefault;

    @Mock
    private PasswordManageDataManager passwordManageDataManager;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private UserRepository userRepository;

    private PasswordWebsiteManageServiceDefault passwordService;

    private User testUser;

    void setUp() {
        testUser = createTestUser();

        passwordService = new PasswordWebsiteManageServiceDefault(
            passwordWebsiteManager,
            resetTokenServiceDefault,
            passwordManageDataManager,
            passwordConfiguration,
            userRepository
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

    @Test
    @DisplayName("Should send email successfully and return masked email")
    void testSendEmailWithUpdatePasswordRequest_WithValidData_ShouldReturnMaskedEmail() {
        setUp();

        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto("user@example.com");

        when(passwordWebsiteManager.findUser("user@example.com")).thenReturn(testUser);

        SendEmailToUpdatePasswordResponseDto result =
            passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        assertNotNull(result, "Response DTO should not be null");
        assertNotNull(result.email(), "Masked email should not be null");
        assertThat(result.email())
            .as("Email should be masked")
            .contains("***@example.com");

        verify(passwordWebsiteManager, times(1)).findUser("user@example.com");
        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should mask email correctly for simple addresses")
    void testSendEmailWithUpdatePasswordRequest_ShouldMaskEmailCorrectly() {
        setUp();

        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto("user@example.com");

        when(passwordWebsiteManager.findUser("user@example.com")).thenReturn(testUser);

        SendEmailToUpdatePasswordResponseDto result =
            passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.email())
            .as("Email should start with first character and contain masked part")
            .startsWith("u")
            .contains("u***@example.com");

        verify(passwordWebsiteManager, times(1)).findUser("user@example.com");
    }

    @Test
    @DisplayName("Should send email to user")
    void testSendEmailWithUpdatePasswordRequest_ShouldSendEmail() {
        setUp();

        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto("user@example.com");

        when(passwordWebsiteManager.findUser("user@example.com")).thenReturn(testUser);

        passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should throw BusinessException when user not found by email")
    void testSendEmailWithUpdatePasswordRequest_WhenUserNotFound_ShouldThrowBusinessException() {
        setUp();

        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto("nonexistent@example.com");

        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(passwordWebsiteManager)
            .findUser("nonexistent@example.com");

        assertThrows(
            BusinessException.class,
            () -> passwordService.sendEmailWithUpdatePasswordRequest(requestDto),
            "Should throw BusinessException when user not found"
        );

        verify(passwordWebsiteManager, times(1)).findUser("nonexistent@example.com");
        verify(passwordWebsiteManager, times(0)).sendEmail(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when sending email fails")
    void testSendEmailWithUpdatePasswordRequest_WhenSendEmailFails_ShouldThrowBusinessException() {
        setUp();

        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto("user@example.com");

        when(passwordWebsiteManager.findUser("user@example.com")).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_SMS_CODE))
            .when(passwordWebsiteManager)
            .sendEmail(testUser.getId());

        assertThrows(
            BusinessException.class,
            () -> passwordService.sendEmailWithUpdatePasswordRequest(requestDto),
            "Should throw BusinessException when email sending fails"
        );

        verify(passwordWebsiteManager, times(1)).findUser("user@example.com");
        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should update password by email token successfully")
    void testUpdatePasswordByEmail_WithValidToken_ShouldUpdatePassword() {
        setUp();

        String newPassword = "newPassword123";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, token, tokenId);

        String newHashedPassword = "new-hashed-password";

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(newPassword)).thenReturn(newHashedPassword);

        passwordService.updatePasswordByEmail(requestDto);

        assertThat(testUser.getPasswordHash()).isEqualTo(newHashedPassword);

        verify(resetTokenServiceDefault, times(1)).validateToken(token, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(newPassword);
        verify(passwordConfiguration, times(1)).encodePassword(newPassword);
        verify(userRepository, times(1)).save(testUser);
        verify(resetTokenServiceDefault, times(1)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should validate token before updating password")
    void testUpdatePasswordByEmail_ShouldValidateTokenFirst() {
        setUp();

        String newPassword = "newPassword123";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, token, tokenId);

        String newHashedPassword = "new-hashed-password";

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(newPassword)).thenReturn(newHashedPassword);

        passwordService.updatePasswordByEmail(requestDto);

        verify(resetTokenServiceDefault, times(1)).validateToken(token, tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when token is invalid")
    void testUpdatePasswordByEmail_WhenTokenIsInvalid_ShouldThrowBusinessException() {
        setUp();

        String newPassword = "newPassword123";
        String invalidToken = "invalid-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, invalidToken, tokenId);

        doThrow(new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN))
            .when(resetTokenServiceDefault)
            .validateToken(invalidToken, tokenId);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when token is invalid"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(invalidToken, tokenId);
        verify(passwordManageDataManager, times(0)).checkDataPatterns(anyString());
        verify(userRepository, times(0)).save(any());
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when password pattern validation fails")
    void testUpdatePasswordByEmail_WhenPasswordPatternValidationFails_ShouldThrowBusinessException() {
        setUp();

        String weakPassword = "weak";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(weakPassword, token, tokenId);

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
            .when(passwordManageDataManager)
            .checkDataPatterns(weakPassword);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when password pattern validation fails"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(token, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(weakPassword);
        verify(passwordConfiguration, times(0)).encodePassword(anyString());
        verify(userRepository, times(0)).save(any());
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void testUpdatePasswordByEmail_WhenUserSaveFails_ShouldThrowBusinessException() {
        setUp();

        String newPassword = "newPassword123";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, token, tokenId);

        String newHashedPassword = "new-hashed-password";

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(newPassword)).thenReturn(newHashedPassword);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userRepository)
            .save(testUser);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when user save fails"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(token, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(newPassword);
        verify(passwordConfiguration, times(1)).encodePassword(newPassword);
        verify(userRepository, times(1)).save(testUser);
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should delete reset token after successful password update")
    void testUpdatePasswordByEmail_ShouldDeleteTokenAfterSuccessfulUpdate() {
        setUp();

        String newPassword = "newPassword123";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, token, tokenId);

        String newHashedPassword = "new-hashed-password";

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(newPassword)).thenReturn(newHashedPassword);

        passwordService.updatePasswordByEmail(requestDto);

        verify(resetTokenServiceDefault, times(1)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void testUpdatePasswordByEmail_ShouldEncodePasswordBeforeSaving() {
        setUp();

        String newPassword = "newPassword123";
        String token = "valid-reset-token";
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(newPassword, token, tokenId);

        String newHashedPassword = "new-hashed-password";

        when(resetTokenServiceDefault.validateToken(token, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(newPassword)).thenReturn(newHashedPassword);

        passwordService.updatePasswordByEmail(requestDto);

        verify(passwordConfiguration, times(1)).encodePassword(newPassword);
        assertThat(testUser.getPasswordHash()).isEqualTo(newHashedPassword);
    }
}
