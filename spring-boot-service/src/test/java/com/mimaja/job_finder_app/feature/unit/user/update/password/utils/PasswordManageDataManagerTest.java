package com.mimaja.job_finder_app.feature.unit.user.update.password.utils;

import static com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata.PasswordManageMockData.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordManageDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordManageDataManager - Unit Tests")
class PasswordManageDataManagerTest {

    private final PasswordManageDataManager passwordManageDataManager = new PasswordManageDataManager();

    // =========================
    // checkDataPatterns - Valid Password Tests
    // =========================

    @Test
    @DisplayName("Should not throw exception when password meets all requirements")
    void testCheckDataPatterns_shouldNotThrowException_whenPasswordIsValid() {
        // when & then
        assertDoesNotThrow(
                () -> passwordManageDataManager.checkDataPatterns(VALID_PASSWORD_WITH_ALL_REQUIREMENTS));
    }

    @Test
    @DisplayName("Should not throw exception when password is minimum valid length")
    void testCheckDataPatterns_shouldNotThrowException_whenPasswordIsMinimumLength() {
        // when & then
        assertDoesNotThrow(
                () -> passwordManageDataManager.checkDataPatterns(VALID_PASSWORD_MINIMUM_LENGTH));
    }

    @Test
    @DisplayName("Should not throw exception when password is long with all requirements")
    void testCheckDataPatterns_shouldNotThrowException_whenPasswordIsLongWithAllRequirements() {
        // when & then
        assertDoesNotThrow(
                () -> passwordManageDataManager.checkDataPatterns(VALID_PASSWORD_LONG));
    }

    // =========================
    // checkDataPatterns - Invalid Length Tests
    // =========================

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_LENGTH when password is too short")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordTooShort() {
        // when & then
        assertThatThrownBy(
                () -> passwordManageDataManager.checkDataPatterns(INVALID_PASSWORD_TOO_SHORT))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for password too short");
    }

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_LENGTH when password is too long")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordTooLong() {
        // when & then
        assertThatThrownBy(
                () -> passwordManageDataManager.checkDataPatterns(INVALID_PASSWORD_TOO_LONG))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for password too long");
    }

    @Test
    @DisplayName("Should throw correct exception code for invalid password length")
    void testCheckDataPatterns_shouldThrowCorrectExceptionCode_whenPasswordLengthInvalid() {
        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () ->
                                passwordManageDataManager.checkDataPatterns(
                                        INVALID_PASSWORD_TOO_SHORT));

        assertThat(exception.getMessage())
                .as("Exception reason should be INVALID_PASSWORD_LENGTH")
                .isEqualTo(BusinessExceptionReason.INVALID_PASSWORD_LENGTH.getMessage());
    }

    // =========================
    // checkDataPatterns - Invalid Pattern Tests
    // =========================

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_PATTERN when password has no lowercase letters")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordHasNoLowercase() {
        // when & then
        assertThatThrownBy(
                () ->
                        passwordManageDataManager.checkDataPatterns(
                                INVALID_PASSWORD_NO_LOWERCASE))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when password has no lowercase letters");
    }

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_PATTERN when password has no uppercase letters")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordHasNoUppercase() {
        // when & then
        assertThatThrownBy(
                () ->
                        passwordManageDataManager.checkDataPatterns(
                                INVALID_PASSWORD_NO_UPPERCASE))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when password has no uppercase letters");
    }

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_PATTERN when password has no digits")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordHasNoDigits() {
        // when & then
        assertThatThrownBy(
                () ->
                        passwordManageDataManager.checkDataPatterns(
                                INVALID_PASSWORD_NO_DIGITS))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when password has no digits");
    }

    @Test
    @DisplayName("Should throw INVALID_PASSWORD_PATTERN when password has no uppercase and no digits")
    void testCheckDataPatterns_shouldThrowBusinessException_whenPasswordHasNoUppercaseAndNoDigits() {
        // when & then
        assertThatThrownBy(
                () ->
                        passwordManageDataManager.checkDataPatterns(
                                INVALID_PASSWORD_NO_UPPERCASE_NO_DIGITS))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when password lacks uppercase letters and digits");
    }

    @Test
    @DisplayName("Should throw correct exception code for invalid password pattern")
    void testCheckDataPatterns_shouldThrowCorrectExceptionCode_whenPasswordPatternInvalid() {
        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () ->
                                passwordManageDataManager.checkDataPatterns(
                                        INVALID_PASSWORD_NO_DIGITS));

        assertThat(exception.getMessage())
                .as("Exception reason should be INVALID_PASSWORD_PATTERN")
                .isEqualTo(BusinessExceptionReason.INVALID_PASSWORD_PATTERN.getMessage());
    }

    @Test
    @DisplayName("Should throw correct exception code for password with only lowercase letters")
    void testCheckDataPatterns_shouldThrowPatternException_whenPasswordHasOnlyLowercase() {
        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () ->
                                passwordManageDataManager.checkDataPatterns(
                                        INVALID_PASSWORD_ONLY_LOWERCASE));

        assertThat(exception.getMessage())
                .as("Exception reason should be INVALID_PASSWORD_PATTERN")
                .isEqualTo(BusinessExceptionReason.INVALID_PASSWORD_PATTERN.getMessage());
    }
}
