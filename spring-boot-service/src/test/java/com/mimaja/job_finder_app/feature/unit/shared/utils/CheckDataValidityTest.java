package com.mimaja.job_finder_app.feature.unit.shared.utils;

import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_EMAIL_NO_AT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_EMAIL_NO_DOMAIN;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_EMAIL_NO_TLD;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PASSWORD_NO_DIGIT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PASSWORD_NO_LOWERCASE;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PASSWORD_NO_UPPERCASE;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PASSWORD_TOO_LONG;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PASSWORD_TOO_SHORT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PHONE_NUMBER_TOO_LONG;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_PHONE_NUMBER_TOO_SHORT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_REST_DATA_EMPTY;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_REST_DATA_ONLY_NUMBERS;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_USERNAME_NO_LETTERS;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_USERNAME_TOO_LONG;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_USERNAME_TOO_SHORT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.INVALID_USERNAME_WITH_AT;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_GOOGLE_ID;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_PHONE_NUMBER;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_REST_DATA;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.VALID_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata.CheckDataValidityMockData.createTestUserWithId;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.shared.utils.CheckDataValidity;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckDataValidity - Unit Tests")
class CheckDataValidityTest {
    @Mock private UserRepository userRepository;

    private CheckDataValidity checkDataValidity;

    @BeforeEach
    void setUp() {
        checkDataValidity = new CheckDataValidity(userRepository);
    }

    // =========================
    // validateUsername Tests
    // =========================

    @Test
    @DisplayName("Should validate username successfully when username is valid")
    void testValidateUsername_shouldNotThrowException_whenUsernameIsValid() {
        // when & then
        assertDoesNotThrow(() -> checkDataValidity.validateUsername(VALID_USERNAME));
    }

    @Test
    @DisplayName("Should throw exception when username is too short")
    void testValidateUsername_shouldThrowBusinessException_whenUsernameTooShort() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateUsername(INVALID_USERNAME_TOO_SHORT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username too short");
    }

    @Test
    @DisplayName("Should throw exception when username is too long")
    void testValidateUsername_shouldThrowBusinessException_whenUsernameTooLong() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateUsername(INVALID_USERNAME_TOO_LONG))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username too long");
    }

    @Test
    @DisplayName("Should throw exception when username contains no letters")
    void testValidateUsername_shouldThrowBusinessException_whenUsernameHasNoLetters() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateUsername(INVALID_USERNAME_NO_LETTERS))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username with no letters");
    }

    @Test
    @DisplayName("Should throw exception when username contains @ symbol")
    void testValidateUsername_shouldThrowBusinessException_whenUsernameContainsAt() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateUsername(INVALID_USERNAME_WITH_AT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username with @");
    }

    // =========================
    // checkUsername Tests (without userId)
    // =========================

    @Test
    @DisplayName("Should check username successfully when username is available")
    void testCheckUsername_shouldNotThrowException_whenUsernameIsAvailable() {
        // given
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkUsername(VALID_USERNAME));
    }

    @Test
    @DisplayName("Should throw exception when username is already taken")
    void testCheckUsername_shouldThrowBusinessException_whenUsernameAlreadyTaken() {
        // given
        User existingUser = createTestUser();
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkUsername(VALID_USERNAME))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username already taken");
    }

    @Test
    @DisplayName("Should call userRepository.findByUsername once")
    void testCheckUsername_shouldCallFindByUsername_whenCheckingUsername() {
        // given
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.empty());

        // when
        checkDataValidity.checkUsername(VALID_USERNAME);

        // then
        verify(userRepository, times(1)).findByUsername(VALID_USERNAME);
    }

    // =========================
    // checkUsername Tests (with userId)
    // =========================

    @Test
    @DisplayName("Should allow username update when username belongs to same user")
    void testCheckUsername_WithUserId_shouldNotThrowException_whenUsernameOwnedBySameUser() {
        // given
        UUID userId = UUID.randomUUID();
        User existingUser = createTestUserWithId(userId);
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(existingUser));

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkUsername(userId, VALID_USERNAME));
    }

    @Test
    @DisplayName("Should throw exception when username belongs to different user")
    void testCheckUsername_WithUserId_shouldThrowBusinessException_whenUsernameOwnedByOtherUser() {
        // given
        UUID userId = UUID.randomUUID();
        User otherUser = createTestUser();
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkUsername(userId, VALID_USERNAME))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for username taken by other user");
    }

    @Test
    @DisplayName("Should allow username update when username is available")
    void testCheckUsername_WithUserId_shouldNotThrowException_whenUsernameAvailable() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkUsername(userId, VALID_USERNAME));
    }

    // =========================
    // validateEmail Tests
    // =========================

    @Test
    @DisplayName("Should validate email successfully when email is valid")
    void testValidateEmail_shouldNotThrowException_whenEmailIsValid() {
        // when & then
        assertDoesNotThrow(() -> checkDataValidity.validateEmail(VALID_EMAIL));
    }

    @Test
    @DisplayName("Should throw exception when email has no @ symbol")
    void testValidateEmail_shouldThrowBusinessException_whenEmailHasNoAt() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateEmail(INVALID_EMAIL_NO_AT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for email without @");
    }

    @Test
    @DisplayName("Should throw exception when email has no domain")
    void testValidateEmail_shouldThrowBusinessException_whenEmailHasNoDomain() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateEmail(INVALID_EMAIL_NO_DOMAIN))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for email without domain");
    }

    @Test
    @DisplayName("Should throw exception when email has no top-level domain")
    void testValidateEmail_shouldThrowBusinessException_whenEmailHasNoTld() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.validateEmail(INVALID_EMAIL_NO_TLD))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for email without TLD");
    }

    // =========================
    // checkEmail Tests (without userId)
    // =========================

    @Test
    @DisplayName("Should check email successfully when email is available")
    void testCheckEmail_shouldNotThrowException_whenEmailIsAvailable() {
        // given
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkEmail(VALID_EMAIL));
    }

    @Test
    @DisplayName("Should throw exception when email is already taken")
    void testCheckEmail_shouldThrowBusinessException_whenEmailAlreadyTaken() {
        // given
        User existingUser = createTestUser();
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkEmail(VALID_EMAIL))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for email already taken");
    }

    @Test
    @DisplayName("Should call userRepository.findByEmail once")
    void testCheckEmail_shouldCallFindByEmail_whenCheckingEmail() {
        // given
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

        // when
        checkDataValidity.checkEmail(VALID_EMAIL);

        // then
        verify(userRepository, times(1)).findByEmail(VALID_EMAIL);
    }

    // =========================
    // checkEmail Tests (with userId)
    // =========================

    @Test
    @DisplayName("Should allow email update when email belongs to same user")
    void testCheckEmail_WithUserId_shouldNotThrowException_whenEmailOwnedBySameUser() {
        // given
        UUID userId = UUID.randomUUID();
        User existingUser = createTestUserWithId(userId);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingUser));

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkEmail(userId, VALID_EMAIL));
    }

    @Test
    @DisplayName("Should throw exception when email belongs to different user")
    void testCheckEmail_WithUserId_shouldThrowBusinessException_whenEmailOwnedByOtherUser() {
        // given
        UUID userId = UUID.randomUUID();
        User otherUser = createTestUser();
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkEmail(userId, VALID_EMAIL))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for email taken by other user");
    }

    @Test
    @DisplayName("Should allow email update when email is available")
    void testCheckEmail_WithUserId_shouldNotThrowException_whenEmailAvailable() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkEmail(userId, VALID_EMAIL));
    }

    // =========================
    // validatePhoneNumber Tests
    // =========================

    @Test
    @DisplayName("Should validate phone number successfully when phone number is valid")
    void testValidatePhoneNumber_shouldNotThrowException_whenPhoneNumberIsValid() {
        // when & then
        assertDoesNotThrow(() -> checkDataValidity.validatePhoneNumber(VALID_PHONE_NUMBER));
    }

    @Test
    @DisplayName("Should throw exception when phone number is too short")
    void testValidatePhoneNumber_shouldThrowBusinessException_whenPhoneNumberTooShort() {
        // when & then
        assertThatThrownBy(
                        () -> checkDataValidity.validatePhoneNumber(INVALID_PHONE_NUMBER_TOO_SHORT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for phone number too short");
    }

    @Test
    @DisplayName("Should throw exception when phone number is too long")
    void testValidatePhoneNumber_shouldThrowBusinessException_whenPhoneNumberTooLong() {
        // when & then
        assertThatThrownBy(
                        () -> checkDataValidity.validatePhoneNumber(INVALID_PHONE_NUMBER_TOO_LONG))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for phone number too long");
    }

    // =========================
    // checkPhoneNumber Tests (without userId)
    // =========================

    @Test
    @DisplayName("Should check phone number successfully when phone number is available")
    void testCheckPhoneNumber_shouldNotThrowException_whenPhoneNumberIsAvailable() {
        // given
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkPhoneNumber(VALID_PHONE_NUMBER));
    }

    @Test
    @DisplayName("Should throw exception when phone number is already taken")
    void testCheckPhoneNumber_shouldThrowBusinessException_whenPhoneNumberAlreadyTaken() {
        // given
        User existingUser = createTestUser();
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                .thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPhoneNumber(VALID_PHONE_NUMBER))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for phone number already taken");
    }

    @Test
    @DisplayName("Should call userRepository.findByPhoneNumber once")
    void testCheckPhoneNumber_shouldCallFindByPhoneNumber_whenCheckingPhoneNumber() {
        // given
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when
        checkDataValidity.checkPhoneNumber(VALID_PHONE_NUMBER);

        // then
        verify(userRepository, times(1)).findByPhoneNumber(VALID_PHONE_NUMBER);
    }

    // =========================
    // checkPhoneNumber Tests (with userId)
    // =========================

    @Test
    @DisplayName("Should allow phone number update when phone number belongs to same user")
    void testCheckPhoneNumber_WithUserId_shouldNotThrowException_whenPhoneNumberOwnedBySameUser() {
        // given
        UUID userId = UUID.randomUUID();
        User existingUser = createTestUserWithId(userId);
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                .thenReturn(Optional.of(existingUser));

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkPhoneNumber(userId, VALID_PHONE_NUMBER));
    }

    @Test
    @DisplayName("Should throw exception when phone number belongs to different user")
    void
            testCheckPhoneNumber_WithUserId_shouldThrowBusinessException_whenPhoneNumberOwnedByOtherUser() {
        // given
        UUID userId = UUID.randomUUID();
        User otherUser = createTestUser();
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER))
                .thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPhoneNumber(userId, VALID_PHONE_NUMBER))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for phone number taken by other user");
    }

    @Test
    @DisplayName("Should allow phone number update when phone number is available")
    void testCheckPhoneNumber_WithUserId_shouldNotThrowException_whenPhoneNumberAvailable() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.findByPhoneNumber(VALID_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkPhoneNumber(userId, VALID_PHONE_NUMBER));
    }

    // =========================
    // checkGoogleId Tests
    // =========================

    @Test
    @DisplayName("Should check Google ID successfully when Google ID is available")
    void testCheckGoogleId_shouldNotThrowException_whenGoogleIdIsAvailable() {
        // given
        when(userRepository.findByGoogleId(VALID_GOOGLE_ID)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkGoogleId(VALID_GOOGLE_ID));
    }

    @Test
    @DisplayName("Should throw exception when Google ID is already taken")
    void testCheckGoogleId_shouldThrowBusinessException_whenGoogleIdAlreadyTaken() {
        // given
        User existingUser = createTestUser();
        when(userRepository.findByGoogleId(VALID_GOOGLE_ID)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkGoogleId(VALID_GOOGLE_ID))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for Google ID already taken");
    }

    @Test
    @DisplayName("Should call userRepository.findByGoogleId once")
    void testCheckGoogleId_shouldCallFindByGoogleId_whenCheckingGoogleId() {
        // given
        when(userRepository.findByGoogleId(VALID_GOOGLE_ID)).thenReturn(Optional.empty());

        // when
        checkDataValidity.checkGoogleId(VALID_GOOGLE_ID);

        // then
        verify(userRepository, times(1)).findByGoogleId(VALID_GOOGLE_ID);
    }

    // =========================
    // checkPassword Tests
    // =========================

    @Test
    @DisplayName("Should validate password successfully when password is valid")
    void testCheckPassword_shouldNotThrowException_whenPasswordIsValid() {
        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkPassword(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Should throw exception when password is too short")
    void testCheckPassword_shouldThrowBusinessException_whenPasswordTooShort() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPassword(INVALID_PASSWORD_TOO_SHORT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for password too short");
    }

    @Test
    @DisplayName("Should throw exception when password is too long")
    void testCheckPassword_shouldThrowBusinessException_whenPasswordTooLong() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPassword(INVALID_PASSWORD_TOO_LONG))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for password too long");
    }

    @Test
    @DisplayName("Should throw exception when password has no uppercase letter")
    void testCheckPassword_shouldThrowBusinessException_whenPasswordHasNoUppercase() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPassword(INVALID_PASSWORD_NO_UPPERCASE))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for password without uppercase");
    }

    @Test
    @DisplayName("Should throw exception when password has no lowercase letter")
    void testCheckPassword_shouldThrowBusinessException_whenPasswordHasNoLowercase() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPassword(INVALID_PASSWORD_NO_LOWERCASE))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for password without lowercase");
    }

    @Test
    @DisplayName("Should throw exception when password has no digit")
    void testCheckPassword_shouldThrowBusinessException_whenPasswordHasNoDigit() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkPassword(INVALID_PASSWORD_NO_DIGIT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for password without digit");
    }

    // =========================
    // checkRestData Tests
    // =========================

    @Test
    @DisplayName("Should validate rest data successfully when rest data is valid")
    void testCheckRestData_shouldNotThrowException_whenRestDataIsValid() {
        // when & then
        assertDoesNotThrow(() -> checkDataValidity.checkRestData(VALID_REST_DATA));
    }

    @Test
    @DisplayName("Should throw exception when rest data is empty")
    void testCheckRestData_shouldThrowBusinessException_whenRestDataIsEmpty() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkRestData(INVALID_REST_DATA_EMPTY))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for empty rest data");
    }

    @Test
    @DisplayName("Should throw exception when rest data contains only numbers")
    void testCheckRestData_shouldThrowBusinessException_whenRestDataHasOnlyNumbers() {
        // when & then
        assertThatThrownBy(() -> checkDataValidity.checkRestData(INVALID_REST_DATA_ONLY_NUMBERS))
                .isInstanceOf(BusinessException.class)
                .as("Should throw exception for rest data with only numbers");
    }
}
