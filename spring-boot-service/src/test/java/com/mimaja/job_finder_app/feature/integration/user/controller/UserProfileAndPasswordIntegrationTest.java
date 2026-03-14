package com.mimaja.job_finder_app.feature.integration.user.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.passwordMobileUpdatePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.profileCompletionFormPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdateEmailPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdatePhoneNumberPath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_FIRST_NAME;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_LAST_NAME;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_NEW_PASSWORD;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PROFILE_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class UserProfileAndPasswordIntegrationTest extends IntegrationTest {
    private static final String CODE_PATH = "$.code";
    private static final String RESOURCE_UPDATED = "RESOURCE_UPDATED";
    private static final String UPDATED_EMAIL_PREFIX = "updated.";
    private static final int PHONE_INCREMENT = 1;

    @Test
    void ShouldUpdateProfileEmailPhoneAndPassword_WhenAuthenticatedUserProvidesValidData() throws Exception {
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);

        String formPayload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "firstName", DEFAULT_FIRST_NAME,
                                "lastName", DEFAULT_LAST_NAME,
                                "profileDescription", DEFAULT_PROFILE_DESCRIPTION));

        MvcResult formResult =
                mockMvc.perform(
                                post(profileCompletionFormPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(formPayload))
                        .andExpect(status().isOk())
                        .andReturn();
        assertThat((String) JsonPath.read(formResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);

        String updatedEmail = UPDATED_EMAIL_PREFIX + user.email();
        String updateEmailPayload =
                objectMapper.writeValueAsString(
                        Map.of("newEmail", updatedEmail, "password", user.password()));
        MvcResult updateEmailResult =
                mockMvc.perform(
                                patch(userUpdateEmailPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(updateEmailPayload))
                        .andExpect(status().isOk())
                        .andReturn();

        String updatePhonePayload =
                objectMapper.writeValueAsString(
                        Map.of("newPhoneNumber", user.phoneNumber() + PHONE_INCREMENT, "password", user.password()));
        MvcResult updatePhoneResult =
                mockMvc.perform(
                                patch(userUpdatePhoneNumberPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(updatePhonePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        String updatePasswordPayload =
                objectMapper.writeValueAsString(
                        Map.of("password", user.password(), "newPassword", DEFAULT_NEW_PASSWORD));
        MvcResult updatePasswordResult =
                mockMvc.perform(
                                put(passwordMobileUpdatePath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(updatePasswordPayload))
                        .andExpect(status().isOk())
                        .andReturn();

        AuthTokens loginWithNewPassword = loginUser(updatedEmail, DEFAULT_NEW_PASSWORD);

        assertThat((String) JsonPath.read(updateEmailResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
        assertThat((String) JsonPath.read(updatePhoneResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
        assertThat((String) JsonPath.read(updatePasswordResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
        assertThat(loginWithNewPassword.accessToken().isBlank()).isFalse();
    }
}
