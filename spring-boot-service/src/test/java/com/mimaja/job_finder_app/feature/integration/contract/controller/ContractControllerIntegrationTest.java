package com.mimaja.job_finder_app.feature.integration.contract.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.contractByOfferPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.contractPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

class ContractControllerIntegrationTest extends IntegrationTest {
    private static final String CONTRACT_NOT_FOUND = "CONTRACT_NOT_FOUND";

    @Test
    void shouldReturnContractNotFoundCode_whenFetchingNonExistentContract() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownContractId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(contractPathWithId(), unknownContractId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
    }

    @Test
    void shouldReturnContractNotFoundCode_whenDeletingNonExistentContract() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownContractId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                delete(contractPathWithId(), unknownContractId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
    }

    @Test
    void shouldReturnContractNotFoundCode_whenDecliningNonExistentContract() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownContractId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                patch(contractPathWithId() + "/decline", unknownContractId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
    }

    @Test
    void shouldReturnContractNotFoundCode_whenAcceptingNonExistentContract() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownContractId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                post(contractPathWithId() + "/accept", unknownContractId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesContract() throws Exception {
        // given
        UUID contractId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(contractPathWithId(), contractId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- GET /contract/by-offer/{offerId} ---

    @Test
    void shouldReturnOfferNotFoundCode_whenFetchingContractForNonExistentOffer() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();
        String offerNotFound = "OFFER_NOT_FOUND";

        // when
        MvcResult result =
                mockMvc.perform(
                                get(contractByOfferPath(), unknownOfferId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(offerNotFound);
    }
}
