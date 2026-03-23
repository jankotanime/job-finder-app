package com.mimaja.job_finder_app.feature.unit.security.mockdata;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.time.Instant;
import java.util.UUID;

public class SecurityMockData {

    public static final String TEST_SECRET_KEY = "test-secret-key-for-testing-purposes";
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_EMAIL = "user@example.com";
    public static final String TEST_ROLE = "USER";
    public static final int TEST_PHONE_NUMBER = 123456789;
    public static final String TEST_FIRST_NAME = "John";
    public static final String TEST_LAST_NAME = "Doe";
    public static final String TEST_PROFILE_DESCRIPTION = "Test profile";

    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPhoneNumber(TEST_PHONE_NUMBER);
        user.setFirstName(TEST_FIRST_NAME);
        user.setLastName(TEST_LAST_NAME);
        user.setProfileDescription(TEST_PROFILE_DESCRIPTION);
        return user;
    }

    public static User createTestUserWithoutFirstName() {
        User user = createTestUser();
        user.setFirstName(null);
        return user;
    }

    public static User createTestUserWithoutLastName() {
        User user = createTestUser();
        user.setLastName(null);
        return user;
    }

    public static User createTestUserWithoutBothNames() {
        User user = createTestUser();
        user.setFirstName(null);
        user.setLastName(null);
        return user;
    }

    public static String createValidAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createAccessTokenWithoutFirstName(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createAccessTokenWithoutLastName(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withClaim("firstName", user.getFirstName())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createAccessTokenWithoutUsername(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createAccessTokenWithoutEmail(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", user.getPhoneNumber())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createAccessTokenWithoutPhoneNumber(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", 0)
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(TEST_SECRET_KEY));
    }

    public static String createInvalidAccessToken() {
        return "invalid.token.here";
    }

    public static JwtPrincipal createTestJwtPrincipal(User user) {
        return new JwtPrincipal(
                user,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                TEST_ROLE,
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName());
    }
}
