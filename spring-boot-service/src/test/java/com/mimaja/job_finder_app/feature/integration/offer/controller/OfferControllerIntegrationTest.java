package com.mimaja.job_finder_app.feature.integration.offer.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.adminCategoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminTagPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class OfferControllerIntegrationTest extends IntegrationTest {
    private static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";
    private static final String RESPONSE_SUCCESSFUL = "RESPONSE_SUCCESSFUL";
    private static final String RESOURCE_CREATED = IntegrationTestConstants.RESOURCE_CREATED;
    private static final String RESOURCE_UPDATED = "RESOURCE_UPDATED";
    private static final String DATA_ID_PATH = "$.data.id";
    private static final String OFFER_TEST_CATEGORY = "OfferIntegrationCat";
    private static final String OFFER_TEST_TAG = "OfferIntegrationTag";
    private static final String CATEGORY_COLOR = "BLUE";
    private static final String OFFER_CREATE_TITLE = "Integration offer title";
    private static final String OFFER_CREATE_DESCRIPTION = "Integration offer description";
    private static final String OFFER_CREATE_SALARY = "2500.0";
    private static final String OFFER_CREATE_MAX_APPLICATIONS = "8";
    private static final int OFFER_CREATE_DATE_PLUS_DAYS = 7;
    private static final String OFFER_ORIGINAL_TITLE = "Original title";
    private static final String OFFER_ORIGINAL_DESCRIPTION = "Original description";
    private static final String OFFER_ORIGINAL_SALARY = "1000.0";
    private static final String OFFER_ORIGINAL_MAX_APPLICATIONS = "5";
    private static final String OFFER_UPDATED_TITLE = "Updated title";
    private static final String OFFER_UPDATED_DESCRIPTION = "Updated description";
    private static final String OFFER_UPDATED_SALARY = "3000.0";
    private static final String OFFER_UPDATED_MAX_APPLICATIONS = "12";
    private static final int OFFER_UPDATE_DATE_PLUS_DAYS = 14;

    // --- GET /offer ---

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesOffers() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerPath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESPONSE_SUCCESSFUL);
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesOffers() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get(offerPath()));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- GET /offer/{offerId} ---

    @Test
    void shouldReturnOfferNotFoundCode_whenFetchingNonExistentOffer() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(offerPathWithId(), unknownOfferId)
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
    void shouldReturn401_whenUnauthenticatedUserFetchesOfferById() throws Exception {
        // given
        UUID offerId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(offerPathWithId(), offerId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- DELETE /offer/{offerId} ---

    @Test
    void shouldReturnOfferNotFoundCode_whenDeletingNonExistentOffer() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownOfferId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                delete(offerPathWithId(), unknownOfferId)
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
    void shouldReturn401_whenUnauthenticatedUserDeletesOffer() throws Exception {
        // given
        UUID offerId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(delete(offerPathWithId(), offerId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnResourceCreated_whenUserCreatesOfferWithoutPhoto() throws Exception {
        // given
        OfferTestContext context = prepareUserWithCategoryAndTag();
        String dateTime =
                LocalDateTime.now()
                        .plusDays(OFFER_CREATE_DATE_PLUS_DAYS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // when
        MvcResult result =
                mockMvc.perform(
                                multipart(offerPath())
                                        .param("title", OFFER_CREATE_TITLE)
                                        .param("description", OFFER_CREATE_DESCRIPTION)
                                        .param("dateAndTime", dateTime)
                                        .param("salary", OFFER_CREATE_SALARY)
                                        .param("maxApplications", OFFER_CREATE_MAX_APPLICATIONS)
                                        .param("tags", context.tagId())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(context.accessToken())))
                        .andExpect(status().isCreated())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_CREATED);
    }

    @Test
    void shouldReturnResourceUpdated_whenOwnerUpdatesOfferWithoutPhoto() throws Exception {
        // given
        OfferTestContext context = prepareUserWithCategoryAndTag();
        String createDateTime =
                LocalDateTime.now()
                        .plusDays(OFFER_CREATE_DATE_PLUS_DAYS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        MvcResult createResult =
                mockMvc.perform(
                                multipart(offerPath())
                                        .param("title", OFFER_ORIGINAL_TITLE)
                                        .param("description", OFFER_ORIGINAL_DESCRIPTION)
                                        .param("dateAndTime", createDateTime)
                                        .param("salary", OFFER_ORIGINAL_SALARY)
                                        .param("maxApplications", OFFER_ORIGINAL_MAX_APPLICATIONS)
                                        .param("tags", context.tagId())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(context.accessToken())))
                        .andExpect(status().isCreated())
                        .andReturn();

        String offerIdStr =
                JsonPath.read(createResult.getResponse().getContentAsString(), DATA_ID_PATH);
        UUID offerId = UUID.fromString(offerIdStr);
        String updatedDateTime =
                LocalDateTime.now()
                        .plusDays(OFFER_UPDATE_DATE_PLUS_DAYS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // when
        MvcResult updateResult =
                mockMvc.perform(
                                multipart(HttpMethod.PUT, offerPathWithId(), offerId)
                                        .param("title", OFFER_UPDATED_TITLE)
                                        .param("description", OFFER_UPDATED_DESCRIPTION)
                                        .param("dateAndTime", updatedDateTime)
                                        .param("salary", OFFER_UPDATED_SALARY)
                                        .param("maxApplications", OFFER_UPDATED_MAX_APPLICATIONS)
                                        .param("tags", context.tagId())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(context.accessToken())))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat(
                        (String)
                                JsonPath.read(
                                        updateResult.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_UPDATED);
    }

    private String createCategory(String adminToken) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("name", OFFER_TEST_CATEGORY, "color", CATEGORY_COLOR));
        MvcResult result =
                mockMvc.perform(
                                post(adminCategoryPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), DATA_ID_PATH);
    }

    private String createTag(String adminToken, String categoryId) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("name", OFFER_TEST_TAG, "categoryId", categoryId));
        MvcResult result =
                mockMvc.perform(
                                post(adminTagPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), DATA_ID_PATH);
    }

    private record OfferTestContext(String accessToken, String tagId) {}

    private OfferTestContext prepareUserWithCategoryAndTag() throws Exception {
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        String categoryId = createCategory(adminToken);
        String tagId = createTag(adminToken, categoryId);
        return new OfferTestContext(accessToken, tagId);
    }
}
