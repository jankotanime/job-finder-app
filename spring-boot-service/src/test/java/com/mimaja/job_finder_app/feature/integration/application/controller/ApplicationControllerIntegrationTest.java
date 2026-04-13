package com.mimaja.job_finder_app.feature.integration.application.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.offerApplicationAcceptPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerApplicationPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerApplicationPathWithIds;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerApplicationRejectPath;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.test.web.servlet.ResultActions;

class ApplicationControllerIntegrationTest extends IntegrationTest {
    private static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";
    private static final String APPLICATION_NOT_FOUND = "APPLICATION_NOT_FOUND";

    // --- GET /offer/{offerId}/application ---

    @Test
    void shouldReturnOfferNotFoundCode_whenFetchingApplicationsForNonExistentOffer()
            throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerApplicationPath(), unknownOfferId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesApplications() throws Exception {
        // given
        UUID offerId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(offerApplicationPath(), offerId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- GET /offer/{offerId}/application/{applicationId} ---

    @Test
    void shouldReturnApplicationNotFoundCode_whenFetchingNonExistentApplication() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();
        UUID unknownApplicationId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(
                                                offerApplicationPathWithIds(),
                                                unknownOfferId,
                                                unknownApplicationId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(APPLICATION_NOT_FOUND);
    }

    // --- POST /offer/{offerId}/application ---

    @Test
    void shouldReturnOfferNotFoundCode_whenSendingApplicationToNonExistentOffer() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();
        String body = objectMapper.writeValueAsString(Map.of("cvId", UUID.randomUUID().toString()));

        // when
        MvcResult result =
                mockMvc.perform(
                                post(offerApplicationPath(), unknownOfferId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
    }

    // --- PATCH /offer/{offerId}/application/{applicationId}/accept ---

    @Test
    void shouldReturnOfferNotFoundCode_whenAcceptingApplicationForNonExistentOffer()
            throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();
        UUID unknownApplicationId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                patch(
                                                offerApplicationAcceptPath(),
                                                unknownOfferId,
                                                unknownApplicationId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
    }

    // --- PATCH /offer/{offerId}/application/{applicationId}/reject ---

    @Test
    void shouldReturnOfferNotFoundCode_whenRejectingApplicationForNonExistentOffer()
            throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();
        UUID unknownApplicationId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                patch(
                                                offerApplicationRejectPath(),
                                                unknownOfferId,
                                                unknownApplicationId)
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
