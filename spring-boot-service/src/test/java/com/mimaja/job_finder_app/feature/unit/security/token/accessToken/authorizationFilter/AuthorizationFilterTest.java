package com.mimaja.job_finder_app.feature.unit.security.token.accessToken.authorizationFilter;

import static com.mimaja.job_finder_app.feature.unit.security.mockdata.SecurityMockData.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.accessToken.authorizationFilter.AuthorizationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorizationFilter - Unit Tests")
class AuthorizationFilterTest {

    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private AuthorizationFilter authorizationFilter;
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        authorizationFilter = new AuthorizationFilter(TEST_SECRET_KEY, userRepository, objectMapper);
        testUser = createTestUser();
        SecurityContextHolder.clearContext();
    }

    // =========================
    // No Token Tests
    // =========================

    @Test
    @DisplayName("Should pass request to filter chain when no authorization header")
    void testDoFilterInternal_shouldPassToChain_whenNoAuthorizationHeader()
            throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass request to filter chain when authorization header without Bearer prefix")
    void testDoFilterInternal_shouldPassToChain_whenAuthHeaderWithoutBearer()
            throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // =========================
    // Invalid Token Tests
    // =========================

    @Test
    @DisplayName("Should return error when JWT verification fails")
    void testDoFilterInternal_shouldReturnError_whenJWTVerificationFails()
            throws ServletException, IOException {
        // given
        String invalidToken = createInvalidAccessToken();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(writer).write(contains("INVALID_ACCESS_TOKEN"));
    }

    // =========================
    // Valid Token - Profile Completion Form Tests
    // =========================

    @Test
    @DisplayName("Should set security context for profile completion form POST request")
    void testDoFilterInternal_shouldSetSecurityContext_whenProfileCompletionFormPost()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/profile-completion-form");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Should pass to chain after setting context for profile completion form")
    void testDoFilterInternal_shouldPassToChain_whenProfileCompletionFormPost()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/profile-completion-form");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should throw exception when user not found for profile completion form")
    void testDoFilterInternal_shouldReturnError_whenUserNotFoundForProfileCompletion()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/profile-completion-form");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(writer).write(contains("USER_NOT_FOUND"));
    }

    // =========================
    // Valid Token - Refresh Token Rotate Tests
    // =========================

    @Test
    @DisplayName("Should set security context for refresh token rotate POST request")
    void testDoFilterInternal_shouldSetSecurityContext_whenRefreshTokenRotatePost()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/refresh-token/rotate");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Should pass to chain after setting context for refresh token rotate")
    void testDoFilterInternal_shouldPassToChain_whenRefreshTokenRotatePost()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/refresh-token/rotate");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // =========================
    // Valid Token - Missing Names Tests
    // =========================

    @Test
    @DisplayName("Should return error when firstName is missing on regular request")
    void testDoFilterInternal_shouldReturnError_whenFirstNameMissing()
            throws ServletException, IOException {
        // given
        User userWithoutFirstName = createTestUserWithoutFirstName();
        String token = createValidAccessToken(userWithoutFirstName);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(request.getMethod()).thenReturn("GET");
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(writer).write(contains("PROFILE_INCOMPLETE"));
    }

    @Test
    @DisplayName("Should return error when lastName is missing on regular request")
    void testDoFilterInternal_shouldReturnError_whenLastNameMissing()
            throws ServletException, IOException {
        // given
        User userWithoutLastName = createTestUserWithoutLastName();
        String token = createValidAccessToken(userWithoutLastName);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(request.getMethod()).thenReturn("GET");
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(writer).write(contains("PROFILE_INCOMPLETE"));
    }

    // =========================
    // Valid Token - Regular Request Tests
    // =========================

    @Test
    @DisplayName("Should set security context for regular GET request with complete profile")
    void testDoFilterInternal_shouldSetSecurityContext_whenRegularGetRequest()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Should pass to chain after setting context for regular request")
    void testDoFilterInternal_shouldPassToChain_whenRegularGetRequest()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should set principal with correct user data")
    void testDoFilterInternal_shouldSetPrincipalWithCorrectData_whenValidToken()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/offers");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(com.mimaja.job_finder_app.shared.record.JwtPrincipal.class);
    }

    // =========================
    // Missing Claims Tests
    // =========================

    @Test
    @DisplayName("Should pass to chain when username is missing")
    void testDoFilterInternal_shouldPassToChain_whenUsernameMissing()
            throws ServletException, IOException {
        // given
        String token = createAccessTokenWithoutUsername(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass to chain when email is missing")
    void testDoFilterInternal_shouldPassToChain_whenEmailMissing()
            throws ServletException, IOException {
        // given
        String token = createAccessTokenWithoutEmail(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass to chain when phoneNumber is invalid (zero)")
    void testDoFilterInternal_shouldPassToChain_whenPhoneNumberMissing()
            throws ServletException, IOException {
        // given
        String token = createAccessTokenWithoutPhoneNumber(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // =========================
    // Request URI Path Tests
    // =========================

    @Test
    @DisplayName("Should recognize profile completion form by requestURI")
    void testDoFilterInternal_shouldSetSecurityContext_whenProfileCompletionByRequestURI()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/");
        when(request.getRequestURI()).thenReturn("/profile-completion-form");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Should recognize refresh token rotate by requestURI")
    void testDoFilterInternal_shouldSetSecurityContext_whenRefreshTokenRotateByRequestURI()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/");
        when(request.getRequestURI()).thenReturn("/refresh-token/rotate");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    // =========================
    // Different HTTP Methods Tests
    // =========================

    @Test
    @DisplayName("Should not treat GET request as profile completion form even if path matches")
    void testDoFilterInternal_shouldRequirePostForProfileCompletion_whenGetRequest()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/profile-completion-form");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("Should set context with full principal for POST to regular path")
    void testDoFilterInternal_shouldSetFullPrincipal_whenPostToRegularPath()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/offers");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    // =========================
    // Valid Token - Complete Branch Coverage Tests
    // =========================

    @Test
    @DisplayName("Should pass to chain and set context without profile completion form check")
    void testDoFilterInternal_shouldPassToChainWithContext_whenRegularGetRequestWithProfile()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-other-path");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should set authentication context without filtering for profile form")
    void testDoFilterInternal_shouldSetContextAndPassChain_whenNotProfileCompletionPost()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/offers");
        when(request.getRequestURI()).thenReturn("/offers");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(com.mimaja.job_finder_app.shared.record.JwtPrincipal.class);
    }

    @Test
    @DisplayName("Should set context and not pass chain when authentication set successfully")
    void testDoFilterInternal_shouldSetContextAndPassChain_whenUserFoundForRegularRequest()
            throws ServletException, IOException {
        // given
        String token = createValidAccessToken(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/offers");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass chain without authentication when no valid claims present")
    void testDoFilterInternal_shouldPassChain_whenNoValidClaimsInToken()
            throws ServletException, IOException {
        // given
        String token = createAccessTokenWithoutUsername(testUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/offers");

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass chain when idString is null")
    void testDoFilterInternal_shouldPassChain_whenIdStringIsNull()
            throws ServletException, IOException {
        // given
        // Create token without subject (id)
        String token = JWT.create()
                .withClaim("username", TEST_USERNAME)
                .withClaim("email", TEST_EMAIL)
                .withClaim("role", TEST_ROLE)
                .withClaim("phoneNumber", TEST_PHONE_NUMBER)
                .withClaim("firstName", TEST_FIRST_NAME)
                .withClaim("lastName", TEST_LAST_NAME)
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(TEST_SECRET_KEY));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when
        authorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
