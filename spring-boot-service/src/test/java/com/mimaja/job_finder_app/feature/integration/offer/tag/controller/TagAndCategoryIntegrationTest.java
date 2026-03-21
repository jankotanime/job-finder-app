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
    void shouldReturnCategoryName_whenAdminCreatesAndUserFetchesCategory() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        String userToken = createUserAccessToken(IntegrationTestUsers.next());
        String categoryId = createCategory(adminToken);

        // when
        MvcResult result =
                mockMvc.perform(
                                get(categoryPathWithId(), categoryId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CATEGORY_NAME_PATH))
                .isEqualTo(CATEGORY_NAME);
    }

    @Test
    void shouldReturnTagName_whenAdminCreatesAndUserFetchesTag() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        String userToken = createUserAccessToken(IntegrationTestUsers.next());
        String categoryId = createCategory(adminToken);
        String tagId = createTag(adminToken, categoryId);

        // when
        MvcResult result =
                mockMvc.perform(
                                get(tagPathWithId(), tagId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), TAG_NAME_PATH))
                .isEqualTo(TAG_NAME);
    }

    @Test
    void shouldReturnTagCategoryName_whenAdminCreatesAndUserFetchesTag() throws Exception {
        // given
        String adminToken = createAdminAccessToken(IntegrationTestUsers.next());
        String userToken = createUserAccessToken(IntegrationTestUsers.next());
        String categoryId = createCategory(adminToken);
        String tagId = createTag(adminToken, categoryId);

        // when
        MvcResult result =
                mockMvc.perform(
                                get(tagPathWithId(), tagId)
                                        .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), TAG_CATEGORY_NAME_PATH))
                .isEqualTo(CATEGORY_NAME);
    }

    private String createCategory(String adminToken) throws Exception {
        String payload =
                objectMapper.writeValueAsString(
                        Map.of("name", CATEGORY_NAME, "color", CATEGORY_COLOR));
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
                        Map.of("name", TAG_NAME, "categoryId", categoryId));
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
}
