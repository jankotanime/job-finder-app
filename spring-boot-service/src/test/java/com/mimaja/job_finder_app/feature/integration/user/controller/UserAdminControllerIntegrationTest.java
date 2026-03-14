package com.mimaja.job_finder_app.feature.integration.user.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.adminUserPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminUserPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.DEFAULT_PROFILE_DESCRIPTION;
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

class UserAdminControllerIntegrationTest extends IntegrationTest {
    private static final String CODE_PATH = "$.code";
    private static final String USERNAME_PATH = "$.data.username";
    private static final String USER_ID_PATH = "$.data.id";
    private static final String TOTAL_ELEMENTS_PATH = "$.data.totalElements";
    private static final String RESOURCE_CREATED = "RESOURCE_CREATED";
    private static final long MIN_TOTAL_ELEMENTS = 1L;
    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Smith";
    private static final String UPDATED_USERNAME = "updated_user_123";

    @Test
    void ShouldCreateUpdateAndDeleteUser_WhenAdminCallsCrudEndpoints() throws Exception {
        String adminAccessToken = createAdminAccessToken(IntegrationTestUsers.next());
        TestUserCredentials managedUser = IntegrationTestUsers.next();

        String createPayload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "username", managedUser.username(),
                                "email", managedUser.email(),
                                "phoneNumber", managedUser.phoneNumber(),
                                "password", managedUser.password(),
                                "firstName", FIRST_NAME,
                                "lastName", LAST_NAME,
                                "profileDescription", DEFAULT_PROFILE_DESCRIPTION));

        MvcResult createResult =
                mockMvc.perform(
                                post(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(createPayload))
                        .andExpect(status().isCreated())
                        .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        String createdUserId = JsonPath.read(createResponse, USER_ID_PATH);
        assertThat((String) JsonPath.read(createResponse, CODE_PATH)).isEqualTo(RESOURCE_CREATED);
        assertThat((String) JsonPath.read(createResponse, USERNAME_PATH))
                .isEqualTo(managedUser.username());
        assertThat(createdUserId).isNotBlank();

        MvcResult listResult =
                mockMvc.perform(
                                get(adminUserPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        Number totalElements =
                JsonPath.read(listResult.getResponse().getContentAsString(), TOTAL_ELEMENTS_PATH);
        assertThat(totalElements.longValue()).isGreaterThanOrEqualTo(MIN_TOTAL_ELEMENTS);

        String updatedUsername = UPDATED_USERNAME;
        String updatePayload =
                objectMapper.writeValueAsString(
                        Map.of(
                                "username", updatedUsername,
                                "email", managedUser.email(),
                                "phoneNumber", managedUser.phoneNumber(),
                                "firstName", FIRST_NAME,
                                "lastName", LAST_NAME,
                                "profileDescription", DEFAULT_PROFILE_DESCRIPTION));

        MvcResult updateResult =
                mockMvc.perform(
                                put(adminUserPathWithId(), createdUserId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(updatePayload))
                        .andExpect(status().isOk())
                        .andReturn();

        assertThat((String) JsonPath.read(updateResult.getResponse().getContentAsString(), USERNAME_PATH))
                .isEqualTo(updatedUsername);

        mockMvc.perform(
                        delete(adminUserPathWithId(), createdUserId)
                                .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken)))
                .andExpect(status().isOk());
    }
}
