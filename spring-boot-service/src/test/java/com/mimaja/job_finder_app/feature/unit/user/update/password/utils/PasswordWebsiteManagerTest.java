package com.mimaja.job_finder_app.feature.unit.user.update.password.utils;

import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.INVALID_LOGIN_DATA;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.INVALID_PHONE_NUMBER;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.VALID_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.VALID_PHONE_NUMBER;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.VALID_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.createTestResetTokenResponse;
import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordWebsiteManager;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenServiceDefault;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordWebsiteManager - Unit Tests")
class PasswordWebsiteManagerTest {
    @Mock private UserRepository userRepository;

    @Mock private ResetTokenServiceDefault resetTokenServiceDefault;

    private PasswordWebsiteManager passwordWebsiteManager;

    private User testUser;
    private ResetTokenResponseDto testResetTokenResponse;

    @BeforeEach
    void setUp() {
        passwordWebsiteManager =
                new PasswordWebsiteManager(userRepository, resetTokenServiceDefault);
        testUser = createTestUser();
        testResetTokenResponse = createTestResetTokenResponse();
        ReflectionTestUtils.setField(passwordWebsiteManager, "ssrUrl", "https://example.com");
    }

    // =========================
    // findUser - By Username Tests
    // =========================

    @Test
    @DisplayName("Should find user by username when username exists")
    void testFindUser_shouldReturnUser_whenUserFoundByUsername() {
        // given
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(testUser));

        // when
        User result = passwordWebsiteManager.findUser(VALID_USERNAME);

        // then
        assertThat(result).as("User should be found by username").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByUsername method when finding user by username")
    void testFindUser_shouldCallFindByUsername_whenUserFoundByUsername() {
        // given
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(testUser));

        // when
        passwordWebsiteManager.findUser(VALID_USERNAME);

        // then
        verify(userRepository, times(1)).findByUsername(VALID_USERNAME);
    }

    // =========================
    // findUser - By Email Tests
    // =========================

    @Test
    @DisplayName("Should find user by email when username not found and email exists")
    void testFindUser_shouldReturnUser_whenUserFoundByEmail() {
        // given
        when(userRepository.findByUsername(VALID_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        User result = passwordWebsiteManager.findUser(VALID_EMAIL);

        // then
        assertThat(result).as("User should be found by email").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByEmail method when username search fails")
    void testFindUser_shouldCallFindByEmail_whenUserNotFoundByUsername() {
        // given
        when(userRepository.findByUsername(VALID_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        passwordWebsiteManager.findUser(VALID_EMAIL);

        // then
        verify(userRepository, times(1)).findByEmail(VALID_EMAIL);
    }

    // =========================
    // findUser - By Phone Number Tests
    // =========================

    @Test
    @DisplayName("Should find user by phone number when username and email not found")
    void testFindUser_shouldReturnUser_whenUserFoundByPhoneNumber() {
        // given
        String phoneNumberString = String.valueOf(VALID_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                .thenReturn(Optional.of(testUser));

        // when
        User result = passwordWebsiteManager.findUser(phoneNumberString);

        // then
        assertThat(result).as("User should be found by phone number").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByPhoneNumber when username and email searches fail")
    void testFindUser_shouldCallFindByPhoneNumber_whenNotFoundByUsernameAndEmail() {
        // given
        String phoneNumberString = String.valueOf(VALID_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                .thenReturn(Optional.of(testUser));

        // when
        passwordWebsiteManager.findUser(phoneNumberString);

        // then
        verify(userRepository, times(1)).findByPhoneNumber(VALID_PHONE_NUMBER);
    }

    // =========================
    // findUser - Invalid Phone Number Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when phone number is valid but user not found")
    void testFindUser_shouldThrowBusinessException_whenPhoneNumberValidButUserNotFound() {
        // given
        String phoneNumberString = String.valueOf(VALID_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> passwordWebsiteManager.findUser(phoneNumberString))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when phone number is valid but user not found");
    }

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when phone number is invalid format")
    void testFindUser_shouldThrowBusinessException_whenPhoneNumberFormatInvalid() {
        // given
        when(userRepository.findByUsername(INVALID_PHONE_NUMBER)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(INVALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> passwordWebsiteManager.findUser(INVALID_PHONE_NUMBER))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for invalid phone number format");
    }

    @Test
    @DisplayName("Should throw correct exception code for invalid phone number")
    void testFindUser_shouldThrowCorrectExceptionCode_whenPhoneNumberFormatInvalid() {
        // given
        when(userRepository.findByUsername(INVALID_PHONE_NUMBER)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(INVALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> passwordWebsiteManager.findUser(INVALID_PHONE_NUMBER));

        assertThat(exception.getMessage())
                .as("Exception reason should be WRONG_LOGIN_DATA")
                .isEqualTo(BusinessExceptionReason.WRONG_LOGIN_DATA.getMessage());
    }

    // =========================
    // findUser - User Not Found Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when user not found in any repository search")
    void testFindUser_shouldThrowBusinessException_whenUserNotFound() {
        // given
        when(userRepository.findByUsername(INVALID_LOGIN_DATA)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(INVALID_LOGIN_DATA)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> passwordWebsiteManager.findUser(INVALID_LOGIN_DATA))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when user not found");
    }

    @Test
    @DisplayName("Should throw correct exception code when user not found")
    void testFindUser_shouldThrowCorrectExceptionCode_whenUserNotFound() {
        // given
        when(userRepository.findByUsername(INVALID_LOGIN_DATA)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(INVALID_LOGIN_DATA)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> passwordWebsiteManager.findUser(INVALID_LOGIN_DATA));

        assertThat(exception.getMessage())
                .as("Exception reason should be WRONG_LOGIN_DATA")
                .isEqualTo(BusinessExceptionReason.WRONG_LOGIN_DATA.getMessage());
    }

    // =========================
    // sendEmail Tests
    // =========================

    @Test
    @DisplayName("Should create reset token when sending email")
    void testSendEmail_shouldCreateResetToken_whenSendingEmail() {
        // given
        UUID userId = UUID.randomUUID();
        when(resetTokenServiceDefault.createToken(userId)).thenReturn(testResetTokenResponse);

        // when
        passwordWebsiteManager.sendEmail(userId);

        // then
        verify(resetTokenServiceDefault, times(1)).createToken(userId);
    }

    @Test
    @DisplayName("Should not throw exception when sending email")
    void testSendEmail_shouldNotThrowException_whenSendingEmail() {
        // given
        UUID userId = UUID.randomUUID();
        when(resetTokenServiceDefault.createToken(userId)).thenReturn(testResetTokenResponse);

        // when & then
        assertDoesNotThrow(() -> passwordWebsiteManager.sendEmail(userId));
    }

    @Test
    @DisplayName("Should use correct userId when creating reset token")
    void testSendEmail_shouldUseProvidedUserId_whenCreatingResetToken() {
        // given
        UUID userId = UUID.randomUUID();
        when(resetTokenServiceDefault.createToken(userId)).thenReturn(testResetTokenResponse);

        // when
        passwordWebsiteManager.sendEmail(userId);

        // then
        verify(resetTokenServiceDefault).createToken(argThat(id -> id.equals(userId)));
    }
}
