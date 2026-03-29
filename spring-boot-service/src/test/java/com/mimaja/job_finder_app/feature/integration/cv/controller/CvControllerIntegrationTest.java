package com.mimaja.job_finder_app.feature.integration.cv.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.cvPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.cvPathWithId;
import static com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestConstants.CODE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

class CvControllerIntegrationTest extends IntegrationTest {
    private static final String CV_NOT_FOUND = "CV_NOT_FOUND";
    private static final String RESOURCE_DELETED = "RESOURCE_DELETED";
    private static final String RESPONSE_SUCCESSFUL = "RESPONSE_SUCCESSFUL";

    @Test
    void shouldReturnCvNotFoundCode_whenFetchingNonExistentCv() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownCvId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                get(cvPathWithId(), unknownCvId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CV_NOT_FOUND);
    }

    @Test
    void shouldReturnResourceDeletedCode_whenDeletingAllCvsForUser() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                delete(cvPath())
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isOk())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(RESOURCE_DELETED);
    }

    @Test
    void shouldReturnCvNotFoundCode_whenDeletingNonExistentCv() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());
        UUID unknownCvId = UUID.randomUUID();

        // when
        MvcResult result =
                mockMvc.perform(
                                delete(cvPathWithId(), unknownCvId)
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                bearerToken(accessToken)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // then
        assertThat((String) JsonPath.read(result.getResponse().getContentAsString(), CODE_PATH))
                .isEqualTo(CV_NOT_FOUND);
    }

    @Test
    void shouldReturnSuccessCode_whenFetchingCvsForUser() throws Exception {
        // given
        String accessToken = createUserAccessToken(IntegrationTestUsers.next());

        // when
        MvcResult result =
                mockMvc.perform(
                                get(cvPath())
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
    void shouldReturn401_whenUnauthenticatedUserFetchesCvs() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get(cvPath()));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenUnauthenticatedUserFetchesCvById() throws Exception {
        // given
        UUID cvId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(get(cvPathWithId(), cvId));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }
}
