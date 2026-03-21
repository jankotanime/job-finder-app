package com.mimaja.job_finder_app.feature.unit.user.update.password.website.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_NEW_HASHED_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_NEW_PASSWORD;
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

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
class PasswordWebsiteManageServiceDefaultTest {

    private static final String TEST_RESET_TOKEN    = "valid-reset-token";
    private static final String TEST_INVALID_TOKEN  = "invalid-token";
    private static final String TEST_WEAK_PASSWORD  = "weak";

    @Mock private PasswordWebsiteManager passwordWebsiteManager;
    @Mock private ResetTokenServiceDefault resetTokenServiceDefault;
    @Mock private PasswordManageDataManager passwordManageDataManager;
    @Mock private PasswordConfiguration passwordConfiguration;
    @Mock private UserRepository userRepository;

    private PasswordWebsiteManageServiceDefault passwordService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUserWithPassword();
        passwordService = new PasswordWebsiteManageServiceDefault(
                passwordWebsiteManager, resetTokenServiceDefault,
                passwordManageDataManager, passwordConfiguration, userRepository);
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldReturnNonNullResponse_whenUserExists() {
        when(passwordWebsiteManager.findUser(TEST_EMAIL)).thenReturn(testUser);
        SendEmailToUpdatePasswordResponseDto result =
                passwordService.sendEmailWithUpdatePasswordRequest(createSendEmailRequest());
        assertThat(result).isNotNull();
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldReturnMaskedEmail_whenUserExists() {
        when(passwordWebsiteManager.findUser(TEST_EMAIL)).thenReturn(testUser);
        SendEmailToUpdatePasswordResponseDto result =
                passwordService.sendEmailWithUpdatePasswordRequest(createSendEmailRequest());
        assertThat(result.email()).contains("***@example.com");
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldCallFindUser_whenSendingEmail() {
        when(passwordWebsiteManager.findUser(TEST_EMAIL)).thenReturn(testUser);
        passwordService.sendEmailWithUpdatePasswordRequest(createSendEmailRequest());
        verify(passwordWebsiteManager, times(1)).findUser(TEST_EMAIL);
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldCallSendEmail_whenUserFound() {
        when(passwordWebsiteManager.findUser(TEST_EMAIL)).thenReturn(testUser);
        passwordService.sendEmailWithUpdatePasswordRequest(createSendEmailRequest());
        verify(passwordWebsiteManager, times(1)).sendEmail(testUser.getId());
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldThrowBusinessException_whenUserNotFound() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(passwordWebsiteManager).findUser("nonexistent@example.com");
        assertThrows(BusinessException.class,
                () -> passwordService.sendEmailWithUpdatePasswordRequest(
                        new SendEmailToUpdatePasswordRequestDto("nonexistent@example.com")));
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldNotCallSendEmail_whenUserNotFound() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(passwordWebsiteManager).findUser("nonexistent@example.com");
        assertThrows(BusinessException.class,
                () -> passwordService.sendEmailWithUpdatePasswordRequest(
                        new SendEmailToUpdatePasswordRequestDto("nonexistent@example.com")));
        verify(passwordWebsiteManager, never()).sendEmail(any());
    }

    @Test
    void sendEmailWithUpdatePasswordRequest_shouldThrowBusinessException_whenSendEmailFails() {
        when(passwordWebsiteManager.findUser(TEST_EMAIL)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_SMS_CODE))
                .when(passwordWebsiteManager).sendEmail(testUser.getId());
        assertThrows(BusinessException.class,
                () -> passwordService.sendEmailWithUpdatePasswordRequest(createSendEmailRequest()));
    }

    @Test
    void updatePasswordByEmail_shouldUpdatePasswordHash_whenTokenIsValid() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        assertThat(testUser.getPasswordHash()).isEqualTo(TEST_NEW_HASHED_PASSWORD);
    }

    @Test
    void updatePasswordByEmail_shouldCallValidateToken_whenUpdatingPassword() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        verify(resetTokenServiceDefault, times(1)).validateToken(TEST_RESET_TOKEN, tokenId);
    }

    @Test
    void updatePasswordByEmail_shouldCheckPatterns_whenUpdatingPassword() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        verify(passwordManageDataManager, times(1)).checkDataPatterns(TEST_NEW_PASSWORD);
    }

    @Test
    void updatePasswordByEmail_shouldEncodePassword_whenUpdatingPassword() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        verify(passwordConfiguration, times(1)).encodePassword(TEST_NEW_PASSWORD);
    }

    @Test
    void updatePasswordByEmail_shouldSaveUser_whenUpdatingPassword() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePasswordByEmail_shouldDeleteToken_whenPasswordUpdatedSuccessfully() {
        String tokenId = setupValidTokenMocks();
        passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId));
        verify(resetTokenServiceDefault, times(1)).deleteToken(tokenId);
    }

    @Test
    void updatePasswordByEmail_shouldThrowBusinessException_whenTokenIsInvalid() {
        String tokenId = UUID.randomUUID().toString();
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN))
                .when(resetTokenServiceDefault).validateToken(TEST_INVALID_TOKEN, tokenId);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(
                        new UpdatePasswordByEmailRequestDto(TEST_NEW_PASSWORD, TEST_INVALID_TOKEN, tokenId)));
    }

    @Test
    void updatePasswordByEmail_shouldNotCheckPatterns_whenTokenIsInvalid() {
        String tokenId = UUID.randomUUID().toString();
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN))
                .when(resetTokenServiceDefault).validateToken(TEST_INVALID_TOKEN, tokenId);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(
                        new UpdatePasswordByEmailRequestDto(TEST_NEW_PASSWORD, TEST_INVALID_TOKEN, tokenId)));
        verify(passwordManageDataManager, never()).checkDataPatterns(anyString());
    }

    @Test
    void updatePasswordByEmail_shouldNotDeleteToken_whenTokenIsInvalid() {
        String tokenId = UUID.randomUUID().toString();
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN))
                .when(resetTokenServiceDefault).validateToken(TEST_INVALID_TOKEN, tokenId);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(
                        new UpdatePasswordByEmailRequestDto(TEST_NEW_PASSWORD, TEST_INVALID_TOKEN, tokenId)));
        verify(resetTokenServiceDefault, never()).deleteToken(tokenId);
    }

    @Test
    void updatePasswordByEmail_shouldThrowBusinessException_whenPatternValidationFails() {
        String tokenId = UUID.randomUUID().toString();
        when(resetTokenServiceDefault.validateToken(TEST_RESET_TOKEN, tokenId)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
                .when(passwordManageDataManager).checkDataPatterns(TEST_WEAK_PASSWORD);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(
                        new UpdatePasswordByEmailRequestDto(TEST_WEAK_PASSWORD, TEST_RESET_TOKEN, tokenId)));
    }

    @Test
    void updatePasswordByEmail_shouldNotEncodePassword_whenPatternValidationFails() {
        String tokenId = UUID.randomUUID().toString();
        when(resetTokenServiceDefault.validateToken(TEST_RESET_TOKEN, tokenId)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN))
                .when(passwordManageDataManager).checkDataPatterns(TEST_WEAK_PASSWORD);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(
                        new UpdatePasswordByEmailRequestDto(TEST_WEAK_PASSWORD, TEST_RESET_TOKEN, tokenId)));
        verify(passwordConfiguration, never()).encodePassword(anyString());
    }

    @Test
    void updatePasswordByEmail_shouldThrowBusinessException_whenSaveFails() {
        String tokenId = setupValidTokenMocks();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userRepository).save(testUser);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId)));
    }

    @Test
    void updatePasswordByEmail_shouldNotDeleteToken_whenSaveFails() {
        String tokenId = setupValidTokenMocks();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userRepository).save(testUser);
        assertThrows(BusinessException.class,
                () -> passwordService.updatePasswordByEmail(createValidEmailRequest(tokenId)));
        verify(resetTokenServiceDefault, never()).deleteToken(tokenId);
    }

    private String setupValidTokenMocks() {
        String tokenId = UUID.randomUUID().toString();
        when(resetTokenServiceDefault.validateToken(TEST_RESET_TOKEN, tokenId)).thenReturn(testUser);
        when(passwordConfiguration.encodePassword(TEST_NEW_PASSWORD)).thenReturn(TEST_NEW_HASHED_PASSWORD);
        return tokenId;
    }

    private SendEmailToUpdatePasswordRequestDto createSendEmailRequest() {
        return new SendEmailToUpdatePasswordRequestDto(TEST_EMAIL);
    }

    private UpdatePasswordByEmailRequestDto createValidEmailRequest(String tokenId) {
        return new UpdatePasswordByEmailRequestDto(TEST_NEW_PASSWORD, TEST_RESET_TOKEN, tokenId);
    }
}
