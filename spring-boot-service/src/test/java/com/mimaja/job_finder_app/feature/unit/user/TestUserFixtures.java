package com.mimaja.job_finder_app.feature.unit.user;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.UUID;

public final class TestUserFixtures {
    public static final String DEFAULT_USERNAME = "testuser";
    public static final String DEFAULT_EMAIL = "user@example.com";
    public static final int DEFAULT_PHONE_NUMBER = 123456789;
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_PROFILE_DESCRIPTION = "Test profile description";

    private TestUserFixtures() {}

    public static User createDefaultUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(DEFAULT_USERNAME);
        user.setEmail(DEFAULT_EMAIL);
        user.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        user.setFirstName(DEFAULT_FIRST_NAME);
        user.setLastName(DEFAULT_LAST_NAME);
        user.setProfileDescription(DEFAULT_PROFILE_DESCRIPTION);
        return user;
    }

    public static User createDefaultUserWithPasswordHash(String passwordHash) {
        User user = createDefaultUser();
        user.setPasswordHash(passwordHash);
        return user;
    }

    public static JwtPrincipal createPrincipal(User user) {
        return JwtPrincipal.from(user);
    }
}
