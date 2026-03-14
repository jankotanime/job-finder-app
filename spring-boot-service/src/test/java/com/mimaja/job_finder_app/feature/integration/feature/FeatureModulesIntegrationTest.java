package com.mimaja.job_finder_app.feature.integration.feature;

import static com.mimaja.job_finder_app.core.test.ApiPath.categoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.contractPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.cvPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobContractorPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobOwnerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerApplicationPathWithIds;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.passwordWebsiteSendEmailPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.tagPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class FeatureModulesIntegrationTest extends IntegrationTest {
    private static final String CODE_PATH = "$.code";
    private static final String RESPONSE_SUCCESSFUL = "RESPONSE_SUCCESSFUL";
    private static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";
    private static final String CONTRACT_NOT_FOUND = "CONTRACT_NOT_FOUND";
    private static final String APPLICATION_NOT_FOUND = "APPLICATION_NOT_FOUND";
    private static final String WRONG_LOGIN_DATA = "WRONG_LOGIN_DATA";
    private static final String UNKNOWN_LOGIN_DATA = "unknown@example.com";

    @Test
    void ShouldReturnSuccessfulResponses_WhenReadingCollectionsAsAuthenticatedUser() throws Exception {
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        MvcResult offerResult =
                mockMvc.perform(get(offerPath()).header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        MvcResult cvResult =
                mockMvc.perform(get(cvPath()).header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        MvcResult ownerJobsResult =
                mockMvc.perform(
                                get(jobOwnerPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        MvcResult contractorJobsResult =
                mockMvc.perform(
                                get(jobContractorPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        MvcResult categoryResult =
                mockMvc.perform(
                                get(categoryPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();
        MvcResult tagResult =
                mockMvc.perform(get(tagPath()).header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        assertThat((String) JsonPath.read(offerResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
        assertThat((String) JsonPath.read(cvResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
        assertThat((String) JsonPath.read(ownerJobsResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
        assertThat((String) JsonPath.read(contractorJobsResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
        assertThat((String) JsonPath.read(categoryResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
        assertThat((String) JsonPath.read(tagResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void ShouldReturnNotFound_WhenResourcesDoNotExistAndUserIsAuthenticated() throws Exception {
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownId = UUID.randomUUID();

        MvcResult offerResult =
                mockMvc.perform(
                                get(offerPathWithId(), unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();
        MvcResult contractResult =
                mockMvc.perform(
                                get(contractPathWithId(), unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();
        MvcResult applicationResult =
                mockMvc.perform(
                                get(offerApplicationPathWithIds(), unknownId, unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        assertThat((String) JsonPath.read(offerResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
        assertThat((String) JsonPath.read(contractResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
        assertThat((String) JsonPath.read(applicationResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(APPLICATION_NOT_FOUND);
    }

    @Test
    void ShouldReturnUnauthorized_WhenPasswordResetEmailIsRequestedForUnknownUser() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of("loginData", UNKNOWN_LOGIN_DATA));

        MvcResult result =
                mockMvc.perform(
                                post(passwordWebsiteSendEmailPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(WRONG_LOGIN_DATA);
    }
}
