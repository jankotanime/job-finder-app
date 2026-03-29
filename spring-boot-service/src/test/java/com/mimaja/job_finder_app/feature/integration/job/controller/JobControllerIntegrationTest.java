package com.mimaja.job_finder_app.feature.integration.job.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.jobContractorPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobCreateFromOfferPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobDispatcherPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobOwnerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobStartPath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.RESPONSE_SUCCESSFUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class JobControllerIntegrationTest extends IntegrationTest {
    private static final String JOB_NOT_FOUND = "JOB_NOT_FOUND";
    private static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";

    @Test
    void shouldReturnJobNotFoundCode_whenFetchingNonExistentJob() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownJobId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobPathWithId(), unknownJobId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(JOB_NOT_FOUND);
    }

    @Test
    void shouldReturnJobNotFoundCode_whenDeletingNonExistentJob() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownJobId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                delete(jobPathWithId(), unknownJobId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(JOB_NOT_FOUND);
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesJob() throws Exception {
        // given
        UUID jobId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(jobPathWithId(), jobId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- GET /job/owner ---

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesOwnerJobs() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobOwnerPath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    // --- GET /job/contractor ---

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesContractorJobs() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobContractorPath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    // --- GET /job/{jobId}/dispatcher ---

    @Test
    void shouldReturnJobNotFoundCode_whenFetchingDispatcherForNonExistentJob() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownJobId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobDispatcherPath(), unknownJobId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(JOB_NOT_FOUND);
    }

    // --- POST /job/{jobId}/start-job ---

    @Test
    void shouldReturnJobNotFoundCode_whenStartingNonExistentJob() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownJobId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                post(jobStartPath(), unknownJobId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(JOB_NOT_FOUND);
    }

    // --- POST /job/{offerId} ---

    @Test
    void shouldReturnOfferNotFoundCode_whenCreatingJobFromNonExistentOffer() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                post(jobCreateFromOfferPath(), unknownOfferId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
    }
}
