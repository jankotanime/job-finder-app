package com.mimaja.job_finder_app.feature.integration.offer.tag.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.adminCategoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminTagPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.categoryPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.tagPathWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class TagAndCategoryIntegrationTest extends IntegrationTest {
    private static final String DATA_ID_PATH = "$.data.id";
    private static final String CATEGORY_NAME_PATH = "$.data.name";
    private static final String TAG_NAME_PATH = "$.data.name";
    private static final String TAG_CATEGORY_NAME_PATH = "$.data.categoryName";
    private static final String CATEGORY_NAME = "IT";
    private static final String CATEGORY_COLOR = "BLUE";
    private static final String TAG_NAME = "Java";

    @Test
    void ShouldCreateCategoryAndTagThenFetchThem_WhenUsingAdminAndUserEndpoints() throws Exception {
        String adminAccessToken = createAdminAccessToken(IntegrationTestUsers.next());
        String userAccessToken = createUserAccessToken(IntegrationTestUsers.next());

        String categoryPayload =
                objectMapper.writeValueAsString(Map.of("name", CATEGORY_NAME, "color", CATEGORY_COLOR));
        MvcResult createCategoryResult =
                mockMvc.perform(
                                post(adminCategoryPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(categoryPayload))
                        .andExpect(status().isCreated())
                        .andReturn();

        String categoryResponse = createCategoryResult.getResponse().getContentAsString();
        String categoryId = JsonPath.read(categoryResponse, DATA_ID_PATH);

        String tagPayload =
                objectMapper.writeValueAsString(Map.of("name", TAG_NAME, "categoryId", categoryId));
        MvcResult createTagResult =
                mockMvc.perform(
                                post(adminTagPath())
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminAccessToken))
                                        .contentType(APPLICATION_JSON)
                                        .content(tagPayload))
                        .andExpect(status().isCreated())
                        .andReturn();

        String tagId = JsonPath.read(createTagResult.getResponse().getContentAsString(), DATA_ID_PATH);

        MvcResult categoryByIdResult =
                mockMvc.perform(
                                get(categoryPathWithId(), categoryId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(userAccessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        MvcResult tagByIdResult =
                mockMvc.perform(
                                get(tagPathWithId(), tagId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(userAccessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        assertThat((String) JsonPath.read(categoryByIdResult.getResponse().getContentAsString(), CATEGORY_NAME_PATH))
                .isEqualTo(CATEGORY_NAME);
        assertThat((String) JsonPath.read(tagByIdResult.getResponse().getContentAsString(), TAG_NAME_PATH))
                .isEqualTo(TAG_NAME);
        assertThat((String) JsonPath.read(tagByIdResult.getResponse().getContentAsString(), TAG_CATEGORY_NAME_PATH))
                .isEqualTo(CATEGORY_NAME);
    }
}
