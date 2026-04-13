package com.mimaja.job_finder_app.feature.integration.global.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.exceptionHttpMessageNotReadablePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.mePath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.RESPONSE_SUCCESSFUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.handler.exception.ErrorCode;
import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class GlobalAndMeIntegrationTest extends IntegrationTest {
    private static final String USERNAME_PATH = "$.data";
    private static final String FIELD_PATH = "$.field";
    private static final String MESSAGE_PATH = "$.message";
    private static final String PROBE_FIELD = "probe";
    private static final String PROBE_MESSAGE = "integration";

    @Test
    void shouldReturnSuccessfulResponseCode_whenAuthenticatedUserCallsMe() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);

        // when
        MvcResult result =
                mockMvc.perform(
                                get(mePath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnUsernameInData_whenAuthenticatedUserCallsMe() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);

        // when
        MvcResult result =
                mockMvc.perform(
                                get(mePath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), USERNAME_PATH))
                .isEqualTo(user.username());
    }

    @Test
    void shouldReturnEchoedField_whenAuthenticatedUserCallsExceptionProbeEndpoint()
            throws Exception {
        // given

        // when
        MvcResult result = whenAuthenticatedUserCallsExceptionProbeEndpoint();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), FIELD_PATH))
                .isEqualTo(PROBE_FIELD);
    }

    @Test
    void shouldReturnEchoedMessage_whenAuthenticatedUserCallsExceptionProbeEndpoint()
            throws Exception {
        // given

        // when
        MvcResult result = whenAuthenticatedUserCallsExceptionProbeEndpoint();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), MESSAGE_PATH))
                .isEqualTo(PROBE_MESSAGE);
    }

    private MvcResult whenAuthenticatedUserCallsExceptionProbeEndpoint() throws Exception {
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        String json = objectMapper.writeValueAsString(buildExceptionProbeRequestBody());
        return mockMvc.perform(
                        get(exceptionHttpMessageNotReadablePath())
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                .contentType(APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andReturn();
    }

    private static FieldValidationErrorsDto buildExceptionProbeRequestBody() {
        return FieldValidationErrorsDto.builder()
                .code(ErrorCode.BAD_REQUEST)
                .field(PROBE_FIELD)
                .message(PROBE_MESSAGE)
                .build();
    }
}
