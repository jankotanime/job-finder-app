package com.mimaja.job_finder_app.feature.integration.user.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.passwordMobileUpdatePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.passwordWebsiteUpdatePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.profileCompletionFormPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdateEmailPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdatePhoneNumberPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdateUserDataPath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_FIRST_NAME;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_LAST_NAME;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_NEW_PASSWORD;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PROFILE_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

class UserProfileAndPasswordIntegrationTest extends IntegrationTest {
    private static final String RESOURCE_UPDATED = "RESOURCE_UPDATED";
    private static final String UPDATED_EMAIL_PREFIX = "updated.";
    private static final int PHONE_INCREMENT = 1;
    private static final String ACCESS_TOKEN_IN_UPDATE_RESPONSE = "$.data.accessToken";
    private static final String UPDATED_PROFILE_DESCRIPTION_FOR_USER_DATA =
            "Updated profile from integration test";
    private static final String NEW_USERNAME_PREFIX = "u";

    @Autowired private ResetTokenService resetTokenService;

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
    void shouldReturnResourceUpdatedCode_whenUserUpdatesUserDataWithoutPhoto() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();

        // when
        MvcResult result = performUserDataUpdateWithoutPhoto(user);

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnNonBlankAccessTokenInResponse_whenUserUpdatesUserDataWithoutPhoto()
            throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();

        // when
        MvcResult result = performUserDataUpdateWithoutPhoto(user);

        // then
        assertThat(
                        (String)
                                JsonPath.read(
                                        result.getResponse().getContentAsString(),
                                        ACCESS_TOKEN_IN_UPDATE_RESPONSE))
                .isNotBlank();
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
    void shouldReturnResourceUpdatedCode_whenPasswordResetViaWebsiteToken() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();

        // when
        MvcResult result = performWebsitePasswordResetUpdate(user);

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    @Test
    void shouldReturnNonBlankAccessToken_whenLoggingInAfterWebsitePasswordReset() throws Exception {
        // given
        TestUserCredentials user = IntegrationTestUsers.next();
        performWebsitePasswordResetUpdate(user);

        // when
        AuthTokens tokens = loginUser(user.email(), DEFAULT_NEW_PASSWORD);

        // then
        assertThat(tokens.accessToken()).isNotBlank();
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

    private MvcResult performUserDataUpdateWithoutPhoto(TestUserCredentials user) throws Exception {
        String accessToken = createUserAccessToken(user);
        String newUsername = NEW_USERNAME_PREFIX + user.phoneNumber();
        return mockMvc.perform(
                        multipart(HttpMethod.PATCH, userUpdateUserDataPath())
                                .param("newUsername", newUsername)
                                .param("newFirstName", DEFAULT_FIRST_NAME)
                                .param("newLastName", DEFAULT_LAST_NAME)
                                .param(
                                        "newProfileDescription",
                                        UPDATED_PROFILE_DESCRIPTION_FOR_USER_DATA)
                                .param("password", user.password())
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult performWebsitePasswordResetUpdate(TestUserCredentials user) throws Exception {
        registerUser(user);
        var savedUser =
                userRepository
                        .findByEmail(user.email())
                        .orElseThrow(() -> new IllegalStateException("User not persisted"));
        var reset = resetTokenService.createToken(savedUser.getId());
        String payload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "password",
                                DEFAULT_NEW_PASSWORD,
                                "token",
                                reset.resetToken(),
                                "tokenId",
                                reset.resetTokenId()));
        return mockMvc.perform(
                        put(passwordWebsiteUpdatePath())
                                .contentType(APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk())
                .andReturn();
    }
}
