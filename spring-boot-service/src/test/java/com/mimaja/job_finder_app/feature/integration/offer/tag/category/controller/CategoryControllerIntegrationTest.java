package com.mimaja.job_finder_app.feature.integration.offer.tag.category.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.categoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.categoryPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class CategoryControllerIntegrationTest extends IntegrationTest {
    private static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
    private static final String RESPONSE_SUCCESSFUL = "RESPONSE_SUCCESSFUL";
    private static final String CATEGORY_FILTER_QUERY_NAME = "integration-filter-name";

    // --- GET /category ---

    @Test
    void shouldReturnSuccessCode_whenAuthenticatedUserFetchesAllCategories() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(categoryPath())
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
    void shouldReturnSuccessCode_whenFilteringCategoriesByName() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(categoryPath())
                                        .param("name", CATEGORY_FILTER_QUERY_NAME)
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
    void shouldReturn401_whenUnauthenticatedUserFetchesCategories() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get(categoryPath()));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    // --- GET /category/{categoryId} ---

    @Test
    void shouldReturnCategoryNotFoundCode_whenFetchingNonExistentCategory() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownCategoryId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(categoryPathWithId(), unknownCategoryId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CATEGORY_NOT_FOUND);
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesCategoryById() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(categoryPathWithId(), categoryId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }
}
