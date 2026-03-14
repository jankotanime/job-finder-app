package com.mimaja.job_finder_app.core.test;

import static com.mimaja.job_finder_app.core.test.ApiPath.authLoginPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.profileCompletionFormPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.authRegisterPath;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import com.mimaja.job_finder_app.feature.user.model.UserRole;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@Tag("IntegrationTest")
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("resource")
public abstract class IntegrationTest {
    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String POSTGRES_DATABASE = "job_finder_app_test";
    private static final String POSTGRES_USERNAME = "test";
    private static final String POSTGRES_PASSWORD = "test";
    private static final String REDIS_IMAGE = "redis:7.2-alpine";
    private static final int REDIS_PORT = 6379;
    private static final String HIBERNATE_CREATE_DROP = "create-drop";
    private static final String GOOGLE_CLIENT_ID = "integration-test-google-client-id";
    private static final String SSR_URL = "http://localhost:3000";
    private static final String CLOUDFLARE_ENDPOINT = "http://localhost:9000";
    private static final String CLOUDFLARE_ACCESS_KEY = "test-access-key";
    private static final String CLOUDFLARE_SECRET_KEY = "test-secret-key";
    private static final String CLOUDFLARE_BUCKET = "test-bucket";
    private static final String SECRET_FILE_PREFIX = "job-finder-secret-";
    private static final String SECRET_FILE_SUFFIX = ".txt";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_PATH = "$.data.accessToken";
    private static final String REFRESH_TOKEN_PATH = "$.data.refreshToken";
    private static final String REFRESH_TOKEN_ID_PATH = "$.data.refreshTokenId";
    private static final String PROFILE_FORM_ACCESS_TOKEN_PATH = "$.data.accessToken";
    private static final String DEFAULT_FIRST_NAME = "Integration";
    private static final String DEFAULT_LAST_NAME = "User";
    private static final String DEFAULT_PROFILE_DESCRIPTION = "Integration test profile";

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName(POSTGRES_DATABASE)
                    .withUsername(POSTGRES_USERNAME)
                    .withPassword(POSTGRES_PASSWORD);

    private static final GenericContainer<?> REDIS =
            new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT);

    private static final Path ACCESS_SECRET = createSecretFile("access-secret");
    private static final Path PASSWORD_SECRET = createSecretFile("bcrypt");
    private static final Path REFRESH_SECRET = createSecretFile("refresh-secret");
    private static final Path RESET_SECRET = createSecretFile("reset-secret");
    private static final Path SMS_SECRET = createSecretFile("sms-secret");

    static {
        Startables.deepStart(Stream.of(POSTGRES, REDIS)).join();
    }

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> HIBERNATE_CREATE_DROP);

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(REDIS_PORT));

        registry.add("access.token.secret", () -> ACCESS_SECRET.toString());
        registry.add("password.secret", () -> PASSWORD_SECRET.toString());
        registry.add("refresh.token.secret", () -> REFRESH_SECRET.toString());
        registry.add("reset.token.secret", () -> RESET_SECRET.toString());
        registry.add("sms.code.secret", () -> SMS_SECRET.toString());

        registry.add("google.id", () -> GOOGLE_CLIENT_ID);
        registry.add("ssr.url", () -> SSR_URL);

        registry.add("cloudflare.r2.endpoint", () -> CLOUDFLARE_ENDPOINT);
        registry.add("cloudflare.r2.accessKey", () -> CLOUDFLARE_ACCESS_KEY);
        registry.add("cloudflare.r2.secretKey", () -> CLOUDFLARE_SECRET_KEY);
        registry.add("cloudflare.r2.bucket", () -> CLOUDFLARE_BUCKET);
    }

    protected AuthTokens registerUser(TestUserCredentials user) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "username", user.username(),
                                "email", user.email(),
                                "phoneNumber", user.phoneNumber(),
                                "password", user.password()));

        MvcResult result =
                mockMvc.perform(post(authRegisterPath()).contentType(APPLICATION_JSON).content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        return extractTokens(result.getResponse().getContentAsString());
    }

    protected AuthTokens loginUser(String loginData, String password) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("loginData", loginData, "password", password));

        MvcResult result =
                mockMvc.perform(post(authLoginPath()).contentType(APPLICATION_JSON).content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        return extractTokens(result.getResponse().getContentAsString());
    }

    protected String createUserAccessToken(TestUserCredentials user) throws Exception {
        AuthTokens authTokens = registerUser(user);
        return completeProfile(authTokens.accessToken());
    }

    protected String createAdminAccessToken(TestUserCredentials user) throws Exception {
        registerUser(user);
        userRepository
                .findByEmail(user.email())
                .ifPresent(
                        savedUser -> {
                            savedUser.setRole(UserRole.ADMIN);
                            savedUser.setFirstName(DEFAULT_FIRST_NAME);
                            savedUser.setLastName(DEFAULT_LAST_NAME);
                            savedUser.setProfileDescription(DEFAULT_PROFILE_DESCRIPTION);
                            userRepository.save(savedUser);
                        });

        return loginUser(user.email(), user.password()).accessToken();
    }

    protected String bearerToken(String accessToken) {
        return BEARER_PREFIX + accessToken;
    }

    protected record AuthTokens(String accessToken, String refreshToken, String refreshTokenId) {}

    private static AuthTokens extractTokens(String responseBody) {
        return new AuthTokens(
                JsonPath.read(responseBody, ACCESS_TOKEN_PATH),
                JsonPath.read(responseBody, REFRESH_TOKEN_PATH),
                JsonPath.read(responseBody, REFRESH_TOKEN_ID_PATH));
    }

    protected String completeProfile(String accessToken) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "firstName", DEFAULT_FIRST_NAME,
                                "lastName", DEFAULT_LAST_NAME,
                                "profileDescription", DEFAULT_PROFILE_DESCRIPTION));

        MvcResult result =
                mockMvc.perform(
                                post(profileCompletionFormPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), PROFILE_FORM_ACCESS_TOKEN_PATH);
    }

    private static Path createSecretFile(String content) {
        try {
            Path file = Files.createTempFile(SECRET_FILE_PREFIX, SECRET_FILE_SUFFIX);
            Files.writeString(file, content);
            file.toFile().deleteOnExit();
            return file;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create secret file for integration tests", exception);
        }
    }
}
