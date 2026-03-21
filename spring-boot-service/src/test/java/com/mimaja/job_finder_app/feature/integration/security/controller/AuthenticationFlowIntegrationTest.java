package com.mimaja.job_finder_app.feature.integration.security.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.authLoginPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.authRegisterPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.refreshRotatePath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PASSWORD;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.RESOURCE_CREATED;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.WRONG_LOGIN_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class AuthenticationFlowIntegrationTest extends IntegrationTest {
    private static final String MESSAGE_PATH = "$.message";
    private static final String ACCESS_TOKEN_PATH = "$.data.accessToken";
    private static final String INVALID_PASSWORD_LENGTH = "INVALID_PASSWORD_LENGTH";
    private static final String INVALID_PASSWORD_LENGTH_MESSAGE =
            "Password should have between 8 and 128 characters";
    private static final String WEAK_PASSWORD = "weak";

    @Test
    void shouldReturnNonBlankAccessToken_whenUserRegisters() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();

        // when
        AuthTokens tokens = registerUser(user);

        // then
        assertThat(tokens.accessToken()).isNotBlank();
    }

    @Test
    void shouldReturnNonBlankAccessToken_whenUserLogsIn() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        registerUser(user);

        // when
        AuthTokens tokens = loginUser(user.username(), user.password());

        // then
        assertThat(tokens.accessToken()).isNotBlank();
    }

    @Test
    void shouldReturnResourceCreatedCode_whenRotatingRefreshToken() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        registerUser(user);
        AuthTokens loginTokens = loginUser(user.username(), user.password());
        String rotatePayload = buildRotatePayload(loginTokens);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(refreshRotatePath())
                                        .contentType(APPLICATION_JSON)
                                        .content(rotatePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_CREATED);
    }

    @Test
    void shouldReturnNonBlankAccessToken_whenRotatingRefreshToken() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        registerUser(user);
        AuthTokens loginTokens = loginUser(user.username(), user.password());
        String rotatePayload = buildRotatePayload(loginTokens);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(refreshRotatePath())
                                        .contentType(APPLICATION_JSON)
                                        .content(rotatePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat(JsonPath.<String>read(result.getResponse().getContentAsString(), ACCESS_TOKEN_PATH))
                .isNotBlank();
    }

    @Test
    void shouldReturnInvalidPasswordLengthCode_whenRegisteringWithWeakPassword() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload = buildRegisterPayloadWithPassword(user, WEAK_PASSWORD);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(authRegisterPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(INVALID_PASSWORD_LENGTH);
    }

    @Test
    void shouldReturnInvalidPasswordLengthMessage_whenRegisteringWithWeakPassword() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload = buildRegisterPayloadWithPassword(user, WEAK_PASSWORD);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(authRegisterPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), MESSAGE_PATH))
                .isEqualTo(INVALID_PASSWORD_LENGTH_MESSAGE);
    }

    @Test
    void shouldReturnWrongLoginDataCode_whenLoginWithWrongPassword() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        registerUser(user);
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("loginData", user.email(), "password", DEFAULT_PASSWORD + "_wrong"));

        // when
        MvcResult result =
                mockMvc.perform(
                                post(authLoginPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(WRONG_LOGIN_DATA);
    }

    private String buildRegisterPayloadWithPassword(TestUserCredentials user, String password)
            throws Exception {
        return objectMapper.writeValueAsString(
                Map.of(
                        "username", user.username(),
                        "email", user.email(),
                        "phoneNumber", user.phoneNumber(),
                        "password", password));
    }

    private String buildRotatePayload(AuthTokens tokens) throws Exception {
        return objectMapper.writeValueAsString(
                Map.of(
                        "refreshToken", tokens.refreshToken(),
                        "refreshTokenId", tokens.refreshTokenId()));
    }
}
