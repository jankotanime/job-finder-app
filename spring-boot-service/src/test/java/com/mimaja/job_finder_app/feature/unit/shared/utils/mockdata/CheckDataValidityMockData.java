package com.mimaja.job_finder_app.feature.unit.shared.utils.mockdata;

import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;

public class CheckDataValidityMockData {
    // Valid test data
    public static final String VALID_USERNAME = "testuser123";
    public static final String VALID_EMAIL = "test@example.com";
    public static final int VALID_PHONE_NUMBER = 123456789;
    public static final String VALID_PASSWORD = "Password123";
    public static final String VALID_GOOGLE_ID = "google_12345678901234567890";
    public static final String VALID_REST_DATA = "SomeValidData123";

    // Invalid username patterns
    public static final String INVALID_USERNAME_NO_LETTERS = "123456789";
    public static final String INVALID_USERNAME_WITH_AT = "test@user";
    public static final String INVALID_USERNAME_TOO_SHORT = "ab1";
    public static final String INVALID_USERNAME_TOO_LONG = "a".repeat(26);

    // Invalid email patterns
    public static final String INVALID_EMAIL_NO_AT = "testexample.com";
    public static final String INVALID_EMAIL_NO_DOMAIN = "test@.com";
    public static final String INVALID_EMAIL_NO_TLD = "test@example";
    public static final String INVALID_EMAIL_EMPTY = "";

    // Invalid phone numbers
    public static final int INVALID_PHONE_NUMBER_TOO_SHORT = 12345678;
    public static final int INVALID_PHONE_NUMBER_TOO_LONG = 1234567890;

    // Invalid passwords
    public static final String INVALID_PASSWORD_NO_UPPERCASE = "password123";
    public static final String INVALID_PASSWORD_NO_LOWERCASE = "PASSWORD123";
    public static final String INVALID_PASSWORD_NO_DIGIT = "PasswordABC";
    public static final String INVALID_PASSWORD_TOO_SHORT = "Pass12";
    public static final String INVALID_PASSWORD_TOO_LONG = "P" + "a".repeat(128) + "1";

    // Invalid rest data
    public static final String INVALID_REST_DATA_EMPTY = "";
    public static final String INVALID_REST_DATA_ONLY_NUMBERS = "123456789";

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
}
