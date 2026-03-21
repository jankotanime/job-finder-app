package com.mimaja.job_finder_app.feature.integration.shared;

import java.util.concurrent.atomic.AtomicInteger;

public final class IntegrationTestUsers {
    private static final AtomicInteger COUNTER = new AtomicInteger(100_000_000);

    private IntegrationTestUsers() {}

    public static TestUserCredentials next() {
        int next = COUNTER.getAndIncrement();
        String suffix = String.valueOf(next);
        return new TestUserCredentials(
                "itest_user_" + suffix,
                "itest_user_" + suffix + "@example.com",
                next,
                IntegrationTestConstants.DEFAULT_PASSWORD);
    }

    public record TestUserCredentials(
            String username, String email, int phoneNumber, String password) {}
}
