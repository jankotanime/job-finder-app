package com.mimaja.job_finder_app.feature.unit.user.update.password.mockdata;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import java.util.UUID;

public class PasswordManageMockData {

    // Valid password patterns
    public static final String VALID_PASSWORD_WITH_ALL_REQUIREMENTS = "Password123";
    public static final String VALID_PASSWORD_WITH_UPPERCASE_AND_DIGITS = "MyPassword456";
    public static final String VALID_PASSWORD_LONG = "LongPassword123WithMoreCharacters456789";
    public static final String VALID_PASSWORD_MINIMUM_LENGTH = "Pass1234";

    // Invalid password patterns - length
    public static final String INVALID_PASSWORD_TOO_SHORT = "Pass123";
    public static final String INVALID_PASSWORD_TOO_LONG = "P" + "a".repeat(128) + "1";

    // Invalid password patterns - missing requirements
    public static final String INVALID_PASSWORD_NO_LOWERCASE = "PASSWORD123";
    public static final String INVALID_PASSWORD_NO_UPPERCASE = "password123";
    public static final String INVALID_PASSWORD_NO_DIGITS = "PasswordABC";
    public static final String INVALID_PASSWORD_NO_UPPERCASE_NO_DIGITS = "password";
    public static final String INVALID_PASSWORD_ONLY_LOWERCASE = "abcdefgh";

    // Valid login data
    public static final String VALID_USERNAME = "testuser";
    public static final String VALID_EMAIL = "test@example.com";
    public static final int VALID_PHONE_NUMBER = 123456789;

    // Invalid login data
    public static final String INVALID_LOGIN_DATA = "nonexistent@example.com";
    public static final String INVALID_PHONE_NUMBER = "notanumber";

    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(VALID_USERNAME);
        user.setEmail(VALID_EMAIL);
        user.setPhoneNumber(VALID_PHONE_NUMBER);
        return user;
    }

    public static User createTestUserWithId(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setUsername(VALID_USERNAME);
        user.setEmail(VALID_EMAIL);
        user.setPhoneNumber(VALID_PHONE_NUMBER);
        return user;
    }

    public static ResetTokenResponseDto createTestResetTokenResponse() {
        return new ResetTokenResponseDto(
                UUID.randomUUID().toString(),
                "reset_token_value_" + System.currentTimeMillis());
    }
}
