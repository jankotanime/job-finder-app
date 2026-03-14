package com.mimaja.job_finder_app.feature.integration.security.controller;

import static com.mimaja.job_finder_app.core.test.ApiPath.adminCategoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminTagPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.adminUserPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.categoryPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.contractPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.cvPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobContractorPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobOwnerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.jobPathWithId;
import static com.mimaja.job_finder_app.core.test.ApiPath.offerPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.passwordMobileUpdatePath;
import static com.mimaja.job_finder_app.core.test.ApiPath.profileCompletionFormPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.tagPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdateEmailPath;
import static com.mimaja.job_finder_app.core.test.ApiPath.userUpdatePhoneNumberPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.mimaja.job_finder_app.core.test.IntegrationTest;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers;
import com.mimaja.job_finder_app.feature.integration.shared.IntegrationTestUsers.TestUserCredentials;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class ProtectedRoutesIntegrationTest extends IntegrationTest {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";
    private static final String METHOD_DELETE = "DELETE";
    private static final String EMPTY_JSON_BODY = "{}";
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_FORBIDDEN = 403;

    @Test
    void ShouldReturnUnauthorized_WhenProtectedEndpointsAreCalledWithoutAccessToken() throws Exception {
        List<RouteCase> protectedRoutes =
                List.of(
                        new RouteCase(METHOD_GET, cvPath()),
                        new RouteCase(METHOD_GET, offerPath()),
                        new RouteCase(METHOD_GET, jobOwnerPath()),
                        new RouteCase(METHOD_GET, jobContractorPath()),
                        new RouteCase(METHOD_GET, categoryPath()),
                        new RouteCase(METHOD_GET, tagPath()),
                        new RouteCase(
                                METHOD_GET,
                                offerPath() + "/" + UUID.randomUUID() + "/application"),
                        new RouteCase(METHOD_POST, profileCompletionFormPath()),
                        new RouteCase(METHOD_PUT, passwordMobileUpdatePath()),
                        new RouteCase(METHOD_PATCH, userUpdateEmailPath()),
                        new RouteCase(METHOD_PATCH, userUpdatePhoneNumberPath()),
                        new RouteCase(METHOD_DELETE, cvPath() + "/" + UUID.randomUUID()),
                        new RouteCase(METHOD_DELETE, offerPath() + "/" + UUID.randomUUID()),
                        new RouteCase(
                                METHOD_GET,
                                contractPathWithId().replace("{contractId}", UUID.randomUUID().toString())),
                        new RouteCase(
                                METHOD_POST,
                                jobPathWithId().replace("{jobId}", UUID.randomUUID().toString())),
                        new RouteCase(METHOD_POST, adminUserPath()),
                        new RouteCase(METHOD_POST, adminTagPath()),
                        new RouteCase(METHOD_POST, adminCategoryPath()));

        for (RouteCase routeCase : protectedRoutes) {
            MvcResult result = mockMvc.perform(buildRequest(routeCase)).andReturn();
            int status = result.getResponse().getStatus();
            assertThat(status)
                    .withFailMessage("Expected 401 for %s %s but got %s", routeCase.method(), routeCase.path(), status)
                    .isEqualTo(HTTP_UNAUTHORIZED);
        }
    }

    @Test
    void ShouldReturnForbidden_WhenUserWithoutAdminRoleCallsAdminEndpoints() throws Exception {
        TestUserCredentials user = IntegrationTestUsers.next();
        String accessToken = createUserAccessToken(user);
        List<RouteCase> adminRoutes =
                List.of(
                        new RouteCase(METHOD_GET, adminUserPath()),
                        new RouteCase(METHOD_POST, adminUserPath()),
                        new RouteCase(METHOD_POST, adminTagPath()),
                        new RouteCase(METHOD_POST, adminCategoryPath()));

        for (RouteCase routeCase : adminRoutes) {
            MvcResult result =
                    mockMvc.perform(buildRequest(routeCase).header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
                            .andReturn();
            assertThat(result.getResponse().getStatus())
                    .withFailMessage(
                            "Expected 403 for %s %s but got %s",
                            routeCase.method(), routeCase.path(), result.getResponse().getStatus())
                    .isEqualTo(HTTP_FORBIDDEN);
        }
    }

    private MockHttpServletRequestBuilder buildRequest(RouteCase routeCase) {
        return switch (routeCase.method()) {
            case METHOD_GET -> get(routeCase.path());
            case METHOD_POST ->
                    post(routeCase.path()).contentType(APPLICATION_JSON).content(EMPTY_JSON_BODY);
            case METHOD_PUT ->
                    put(routeCase.path()).contentType(APPLICATION_JSON).content(EMPTY_JSON_BODY);
            case METHOD_PATCH ->
                    patch(routeCase.path()).contentType(APPLICATION_JSON).content(EMPTY_JSON_BODY);
            case METHOD_DELETE -> delete(routeCase.path());
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + routeCase.method());
        };
    }

    private record RouteCase(String method, String path) {}
}
