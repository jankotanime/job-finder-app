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
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.WRONG_LOGIN_DATA;
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
    private static final String RESPONSE_SUCCESSFUL = "RESPONSE_SUCCESSFUL";
    private static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";
    private static final String CONTRACT_NOT_FOUND = "CONTRACT_NOT_FOUND";
    private static final String APPLICATION_NOT_FOUND = "APPLICATION_NOT_FOUND";
    private static final String UNKNOWN_LOGIN_DATA = "unknown@example.com";

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesOffers() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesCvs() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(cvPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesOwnerJobs() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobOwnerPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesContractorJobs() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(jobContractorPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesCategories() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(categoryPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesTags() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(tagPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturnOfferNotFoundCode_whenOfferDoesNotExist() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerPathWithId(), unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(OFFER_NOT_FOUND);
    }

    @Test
    void shouldReturnContractNotFoundCode_whenContractDoesNotExist() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(contractPathWithId(), unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CONTRACT_NOT_FOUND);
    }

    @Test
    void shouldReturnApplicationNotFoundCode_whenApplicationDoesNotExist() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerApplicationPathWithIds(), unknownId, unknownId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(APPLICATION_NOT_FOUND);
    }

    @Test
    void shouldReturnWrongLoginDataCode_whenPasswordResetRequestedForUnknownEmail() throws Exception {
        // given
        String payload = objectMapper.writeValueAsString(Map.of("loginData", UNKNOWN_LOGIN_DATA));

        // when
        MvcResult result =
                mockMvc.perform(
                                post(passwordWebsiteSendEmailPath())
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(WRONG_LOGIN_DATA);
    }
}
