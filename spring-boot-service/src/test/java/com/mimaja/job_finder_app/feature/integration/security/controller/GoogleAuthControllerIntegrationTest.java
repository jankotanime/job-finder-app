package com.mimaja.job_finder_app.feature.integration.security.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.authGoogleCheckUserExistencePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.authGoogleLoginPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.authGoogleRegisterPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class GoogleAuthControllerIntegrationTest extends IntegrationTest {
    private static final String INVALID_GOOGLE_TOKEN = "invalid-token";
    private static final String INVALID_GOOGLE_ID = "INVALID_GOOGLE_ID";
    private static final String CODE_PATH = "$.code";
    private static final String GOOGLE_TOKEN_KEY = "googleToken";
    private static final String SMS_CODE_KEY = "smsCode";
    private static final String USERNAME_KEY = "username";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String SAMPLE_USERNAME = "new_google_user";
    private static final int SAMPLE_PHONE_NUMBER = 123456789;
    private static final int EMPTY_SMS_CODE = 0;

    @Test
    void ShouldReturnUnauthorized_WhenGoogleLoginTokenIsInvalid() throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(GOOGLE_TOKEN_KEY, INVALID_GOOGLE_TOKEN, SMS_CODE_KEY, EMPTY_SMS_CODE));

        MvcResult result =
                mockMvc.perform(post(authGoogleLoginPath()).contentType(APPLICATION_JSON).content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(INVALID_GOOGLE_ID);
    }

    @Test
    void ShouldReturnUnauthorized_WhenGoogleCheckUserExistenceTokenIsInvalid() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(GOOGLE_TOKEN_KEY, INVALID_GOOGLE_TOKEN));

        MvcResult result =
                mockMvc.perform(
                                post(authGoogleCheckUserExistencePath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(INVALID_GOOGLE_ID);
    }

    @Test
    void ShouldReturnUnauthorized_WhenGoogleRegisterTokenIsInvalid() throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                GOOGLE_TOKEN_KEY,
                                INVALID_GOOGLE_TOKEN,
                                USERNAME_KEY,
                                SAMPLE_USERNAME,
                                PHONE_NUMBER_KEY,
                                SAMPLE_PHONE_NUMBER));

        MvcResult result =
                mockMvc.perform(
                                post(authGoogleRegisterPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(INVALID_GOOGLE_ID);
    }
}
