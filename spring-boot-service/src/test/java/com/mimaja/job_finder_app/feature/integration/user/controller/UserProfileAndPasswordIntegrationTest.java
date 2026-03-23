package com.mimaja.job_finder_app.feature.integration.user.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.passwordMobileUpdatePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.profileCompletionFormPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdateEmailPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdatePhoneNumberPath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
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
    private static final String RESOURCE_UPDATED = "RESOURCE_UPDATED";
    private static final String UPDATED_EMAIL_PREFIX = "updated.";
    private static final int PHONE_INCREMENT = 1;

    @Test
    void shouldReturnResourceUpdatedCode_whenUserCompletesProfile() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "firstName", DEFAULT_FIRST_NAME,
                                "lastName", DEFAULT_LAST_NAME,
                                "profileDescription", DEFAULT_PROFILE_DESCRIPTION));

        // when
        MvcResult result =
                mockMvc.perform(
                                post(profileCompletionFormPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnResourceUpdatedCode_whenUserUpdatesEmail() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        String updatedEmail = UPDATED_EMAIL_PREFIX + user.email();
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("newEmail", updatedEmail, "password", user.password()));

        // when
        MvcResult result =
                mockMvc.perform(
                                patch(userUpdateEmailPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnResourceUpdatedCode_whenUserUpdatesPhoneNumber() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "newPhoneNumber",
                                user.phoneNumber() + PHONE_INCREMENT,
                                "password",
                                user.password()));

        // when
        MvcResult result =
                mockMvc.perform(
                                patch(userUpdatePhoneNumberPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnResourceUpdatedCode_whenUserUpdatesPassword() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("password", user.password(), "newPassword", DEFAULT_NEW_PASSWORD));

        // when
        MvcResult result =
                mockMvc.perform(
                                put(passwordMobileUpdatePath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnNonBlankAccessToken_whenUserLogsInWithNewCredentials() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        String updatedEmail = UPDATED_EMAIL_PREFIX + user.email();

        mockMvc.perform(
                        patch(userUpdateEmailPath())
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "newEmail",
                                                        updatedEmail,
                                                        "password",
                                                        user.password()))))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put(passwordMobileUpdatePath())
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "password",
                                                        user.password(),
                                                        "newPassword",
                                                        DEFAULT_NEW_PASSWORD))))
                .andExpect(status().isOk());

        // when
        AuthTokens tokens = loginUser(updatedEmail, DEFAULT_NEW_PASSWORD);

        // then
        assertThat(tokens.accessToken()).isNotBlank();
    }
}
