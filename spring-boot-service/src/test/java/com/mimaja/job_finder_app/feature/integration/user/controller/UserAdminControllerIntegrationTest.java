package com.mimaja.job_finder_app.feature.integration.user.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.adminUserPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminUserPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PROFILE_DESCRIPTION;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.RESOURCE_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.ResultActions;

class UserAdminControllerIntegrationTest extends IntegrationTest {
    private static final String USERNAME_PATH = "$.data.username";
    private static final String USER_ID_PATH = "$.data.id";
    private static final String TOTAL_ELEMENTS_PATH = "$.data.totalElements";
    private static final long MIN_TOTAL_ELEMENTS = 1L;
    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Smith";
    private static final String UPDATED_USERNAME = "updated_user_123";

    @Test
    void shouldReturnResourceCreatedCode_whenAdminCreatesUser() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload = buildCreateUserPayload(user);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_CREATED);
    }

    @Test
    void shouldReturnNonBlankUserId_whenAdminCreatesUser() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload = buildCreateUserPayload(user);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), USER_ID_PATH))
                .isNotBlank();
    }

    @Test
    void shouldReturnCreatedUsername_whenAdminCreatesUser() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        String payload = buildCreateUserPayload(user);

        // when
        MvcResult result =
                mockMvc.perform(
                                post(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), USERNAME_PATH))
                .isEqualTo(user.username());
    }

    @Test
    void shouldReturnAtLeastOneUser_whenAdminListsUsers() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        mockMvc.perform(
                        post(adminUserPath())
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                .contentType(APPLICATION_JSON)
                                .content(buildCreateUserPayload(user)))
                .andExpect(status().isCreated());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        Number totalElements =
                JsonPath.read(result.getResponse().getContentAsString(), TOTAL_ELEMENTS_PATH);
        assertThat(totalElements.longValue()).isGreaterThanOrEqualTo(MIN_TOTAL_ELEMENTS);
    }

    @Test
    void shouldReturnUpdatedUsername_whenAdminUpdatesUser() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        String createdUserId = createUserAndGetId(adminToken, user);
        String updatePayload = buildUpdateUserPayload(user, UPDATED_USERNAME);

        // when
        MvcResult result =
                mockMvc.perform(
                                put(adminUserPathWithId(), createdUserId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(updatePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), USERNAME_PATH))
                .isEqualTo(UPDATED_USERNAME);
    }

    @Test
    void shouldDeleteUser_whenAdminCallsDeleteEndpoint() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials user = IntegrationTestUsers.next();
        String createdUserId = createUserAndGetId(adminToken, user);

        // when
        ResultActions resultActions =
                mockMvc.perform(
                        delete(adminUserPathWithId(), createdUserId)
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)));

        // then
        resultActions.andExpect(status().isOk());
    }

    private String createUserAndGetId(String adminToken, TestUserCredentials user)
            throws Exception {
        String payload = buildCreateUserPayload(user);
        MvcResult result =
                mockMvc.perform(
                                post(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), USER_ID_PATH);
    }

    private String buildCreateUserPayload(TestUserCredentials user) throws Exception {
        return objectMapper.writeValueAsString(
                Map.of(
                        "username", user.username(),
                        "email", user.email(),
                        "phoneNumber", user.phoneNumber(),
                        "password", user.password(),
                        "firstName", FIRST_NAME,
                        "lastName", LAST_NAME,
                        "profileDescription", DEFAULT_PROFILE_DESCRIPTION));
    }

    private String buildUpdateUserPayload(TestUserCredentials user, String newUsername)
            throws Exception {
        return objectMapper.writeValueAsString(
                Map.of(
                        "username", newUsername,
                        "email", user.email(),
                        "phoneNumber", user.phoneNumber(),
                        "firstName", FIRST_NAME,
                        "lastName", LAST_NAME,
                        "profileDescription", DEFAULT_PROFILE_DESCRIPTION));
    }
}
