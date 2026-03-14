package com.mimaja.job_finder_app.feature.integration.security.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.authLoginPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.authRegisterPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.refreshRotatePath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PASSWORD;
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
    private static final String CODE_PATH = "$.code";
    private static final String MESSAGE_PATH = "$.message";
    private static final String ACCESS_TOKEN_PATH = "$.data.accessToken";
    private static final String RESOURCE_CREATED = "RESOURCE_CREATED";
    private static final String INVALID_PASSWORD_LENGTH = "INVALID_PASSWORD_LENGTH";
    private static final String WRONG_LOGIN_DATA = "WRONG_LOGIN_DATA";
    private static final String WEAK_PASSWORD = "weak";
    private static final String INVALID_PASSWORD_LENGTH_MESSAGE =
            "Password should have between 8 and 128 characters";

    @Test
    void ShouldRegisterThenLoginThenRotateToken_WhenCredentialsAreValid() throws Exception {
        TestUserCredentials user = IntegrationTestUsers.next();
        AuthTokens registerTokens = registerUser(user);
        AuthTokens loginTokens = loginUser(user.username(), user.password());

        String rotatePayload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "refreshToken", loginTokens.refreshToken(),
                                "refreshTokenId", loginTokens.refreshTokenId()));

        MvcResult rotateResult =
                mockMvc.perform(
                                post(refreshRotatePath())
                                        .contentType(APPLICATION_JSON)
                                        .content(rotatePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        String responseBody = rotateResult.getResponse().getContentAsString();
        assertThat(registerTokens.accessToken().isBlank()).isFalse();
        assertThat(loginTokens.accessToken().isBlank()).isFalse();
        assertThat((String) JsonPath.read(responseBody, CODE_PATH)).isEqualTo(RESOURCE_CREATED);
        assertThat(JsonPath.<String>read(responseBody, ACCESS_TOKEN_PATH)).isNotBlank();
    }

    @Test
    void ShouldReturnUnauthorized_WhenRegisterPayloadHasInvalidPasswordPattern() throws Exception {
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "username", user.username(),
                                "email", user.email(),
                                "phoneNumber", user.phoneNumber(),
                                "password", WEAK_PASSWORD));

        MvcResult result =
                mockMvc.perform(post(authRegisterPath()).contentType(APPLICATION_JSON).content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat((String) JsonPath.read(responseBody, CODE_PATH)).isEqualTo(INVALID_PASSWORD_LENGTH);
        assertThat((String) JsonPath.read(responseBody, MESSAGE_PATH))
                .isEqualTo(INVALID_PASSWORD_LENGTH_MESSAGE);
    }

    @Test
    void ShouldReturnUnauthorized_WhenLoginPasswordIsWrong() throws Exception {
        TestUserCredentials user = IntegrationTestUsers.next();
        registerUser(user);

        String payload =
                objectMapper.writeValueAsString(
                        Map.of("loginData", user.email(), "password", DEFAULT_PASSWORD + "_wrong"));

        MvcResult result =
                mockMvc.perform(post(authLoginPath()).contentType(APPLICATION_JSON).content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat((String) JsonPath.read(responseBody, CODE_PATH)).isEqualTo(WRONG_LOGIN_DATA);
    }
}
