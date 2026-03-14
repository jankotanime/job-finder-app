package com.mimaja.job_finder_app.feature.integration.shared;

import java.util.concurrent.atomic.AtomicInteger;

public final class IntegrationTestUsers {
    private static final int INITIAL_COUNTER_VALUE =
            (int) (System.currentTimeMillis() % 800000000L) + 100000000;
    private static final AtomicInteger COUNTER = new AtomicInteger(INITIAL_COUNTER_VALUE);

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

    public record TestUserCredentials(String username, String email, int phoneNumber, String password) {}
}
