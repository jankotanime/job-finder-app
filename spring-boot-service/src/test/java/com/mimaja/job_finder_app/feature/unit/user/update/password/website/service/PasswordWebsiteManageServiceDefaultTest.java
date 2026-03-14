package com.mimaja.job_finder_app.feature.unit.user.update.password.website.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUserWithPasswordHash;
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

import org.junit.jupiter.api.BeforeEach;
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
    private static final String OLD_HASHED_PASSWORD = "old-hashed-password";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String VALID_TOKEN = "valid-reset-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String WEAK_PASSWORD = "weak";
    private static final String NEW_HASHED_PASSWORD = "new-hashed-password";
    private static final String MASKED_EMAIL_SUFFIX = "***@example.com";
    private static final String MASKED_EMAIL_PREFIX = "u***@example.com";


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

    @BeforeEach
    void setUp() {
        testUser = createDefaultUserWithPasswordHash(OLD_HASHED_PASSWORD);

        passwordService = new PasswordWebsiteManageServiceDefault(
            passwordWebsiteManager,
            resetTokenServiceDefault,
            passwordManageDataManager,
            passwordConfiguration,
            userRepository
        );
    }

    @Test
    @DisplayName("Should send email successfully and return masked email")
    void shouldReturnMaskedEmail_WhenSendEmailWithUpdatePasswordRequestWithValidData() {
        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto(DEFAULT_EMAIL);

        when(passwordWebsiteManager.findUser(DEFAULT_EMAIL)).thenReturn(testUser);

        SendEmailToUpdatePasswordResponseDto result =
            passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        assertNotNull(result, "Response DTO should not be null");
        assertNotNull(result.email(), "Masked email should not be null");
        assertThat(result.email())
            .as("Email should be masked")
            .contains(MASKED_EMAIL_SUFFIX);

        verify(passwordWebsiteManager, times(1)).findUser(DEFAULT_EMAIL);
        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should mask email correctly for simple addresses")
    void shouldMaskEmailCorrectly_WhenSendEmailWithUpdatePasswordRequest() {
        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto(DEFAULT_EMAIL);

        when(passwordWebsiteManager.findUser(DEFAULT_EMAIL)).thenReturn(testUser);

        SendEmailToUpdatePasswordResponseDto result =
            passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.email())
            .as("Email should start with first character and contain masked part")
            .startsWith("u")
            .contains(MASKED_EMAIL_PREFIX);

        verify(passwordWebsiteManager, times(1)).findUser(DEFAULT_EMAIL);
    }

    @Test
    @DisplayName("Should send email to user")
    void shouldSendEmail_WhenSendEmailWithUpdatePasswordRequest() {
        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto(DEFAULT_EMAIL);

        when(passwordWebsiteManager.findUser(DEFAULT_EMAIL)).thenReturn(testUser);

        passwordService.sendEmailWithUpdatePasswordRequest(requestDto);

        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should throw BusinessException when user not found by email")
    void shouldThrowBusinessException_WhenUserNotFoundDuringSendEmailWithUpdatePasswordRequest() {
        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto(NON_EXISTENT_EMAIL);

        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(passwordWebsiteManager)
            .findUser(NON_EXISTENT_EMAIL);

        assertThrows(
            BusinessException.class,
            () -> passwordService.sendEmailWithUpdatePasswordRequest(requestDto),
            "Should throw BusinessException when user not found"
        );

        verify(passwordWebsiteManager, times(1)).findUser(NON_EXISTENT_EMAIL);
        verify(passwordWebsiteManager, times(0)).sendEmail(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when sending email fails")
    void shouldThrowBusinessException_WhenSendEmailFailsDuringSendEmailWithUpdatePasswordRequest() {
        SendEmailToUpdatePasswordRequestDto requestDto =
            new SendEmailToUpdatePasswordRequestDto(DEFAULT_EMAIL);

        when(passwordWebsiteManager.findUser(DEFAULT_EMAIL)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_SMS_CODE))
            .when(passwordWebsiteManager)
            .sendEmail(testUser.getId());

        assertThrows(
            BusinessException.class,
            () -> passwordService.sendEmailWithUpdatePasswordRequest(requestDto),
            "Should throw BusinessException when email sending fails"
        );

        verify(passwordWebsiteManager, times(1)).findUser(DEFAULT_EMAIL);
        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    @DisplayName("Should update password by email token successfully")
    void shouldUpdatePassword_WhenUpdatePasswordByEmailWithValidToken() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePasswordByEmail(requestDto);

        assertThat(testUser.getPasswordHash()).isEqualTo(NEW_HASHED_PASSWORD);

        verify(resetTokenServiceDefault, times(1)).validateToken(VALID_TOKEN, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        verify(userRepository, times(1)).save(testUser);
        verify(resetTokenServiceDefault, times(1)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should validate token before updating password")
    void shouldValidateTokenFirst_WhenUpdatePasswordByEmail() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePasswordByEmail(requestDto);

        verify(resetTokenServiceDefault, times(1)).validateToken(VALID_TOKEN, tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when token is invalid")
    void shouldThrowBusinessException_WhenUpdatePasswordByEmailWithInvalidToken() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, INVALID_TOKEN, tokenId);

        doThrow(new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN))
            .when(resetTokenServiceDefault)
            .validateToken(INVALID_TOKEN, tokenId);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when token is invalid"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(INVALID_TOKEN, tokenId);
        verify(passwordManageDataManager, times(0)).checkDataPatterns(anyString());
        verify(userRepository, times(0)).save(any());
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when password pattern validation fails")
    void shouldThrowBusinessException_WhenPasswordPatternValidationFailsDuringUpdatePasswordByEmail() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(WEAK_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
            .when(passwordManageDataManager)
            .checkDataPatterns(WEAK_PASSWORD);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when password pattern validation fails"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(VALID_TOKEN, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(WEAK_PASSWORD);
        verify(passwordConfiguration, times(0)).encodePassword(anyString());
        verify(userRepository, times(0)).save(any());
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void shouldThrowBusinessException_WhenUserSaveFailsDuringUpdatePasswordByEmail() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userRepository)
            .save(testUser);

        assertThrows(
            BusinessException.class,
            () -> passwordService.updatePasswordByEmail(requestDto),
            "Should throw BusinessException when user save fails"
        );

        verify(resetTokenServiceDefault, times(1)).validateToken(VALID_TOKEN, tokenId);
        verify(passwordManageDataManager, times(1)).checkDataPatterns(NEW_PASSWORD);
        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        verify(userRepository, times(1)).save(testUser);
        verify(resetTokenServiceDefault, times(0)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should delete reset token after successful password update")
    void shouldDeleteTokenAfterSuccessfulUpdatePasswordByEmail() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePasswordByEmail(requestDto);

        verify(resetTokenServiceDefault, times(1)).deleteToken(tokenId);
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void shouldEncodePasswordBeforeSaving_WhenUpdatePasswordByEmail() {
        String tokenId = UUID.randomUUID().toString();

        UpdatePasswordByEmailRequestDto requestDto =
            new UpdatePasswordByEmailRequestDto(NEW_PASSWORD, VALID_TOKEN, tokenId);

        when(resetTokenServiceDefault.validateToken(VALID_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);

        passwordService.updatePasswordByEmail(requestDto);

        verify(passwordConfiguration, times(1)).encodePassword(NEW_PASSWORD);
        assertThat(testUser.getPasswordHash()).isEqualTo(NEW_HASHED_PASSWORD);
    }
}
