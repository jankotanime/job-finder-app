package com.mimaja.job_finder_app.security.token.accessToken.authorizationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.shared.dto.ErrorAccessTokenResponseDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private final String secretKey;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            DecodedJWT jwt;
            try {
                jwt =
                        JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(secretKey))
                                .build()
                                .verify(accessToken);
            } catch (JWTVerificationException e) {
                BusinessException ex =
                        new BusinessException(BusinessExceptionReason.INVALID_ACCESS_TOKEN);

                response.getWriter()
                        .write(
                                objectMapper.writeValueAsString(
                                        new ErrorAccessTokenResponseDto(
                                                ex.getCode(),
                                                ex.getMessage(),
                                                LocalDateTime.now(),
                                                null)));
                return;
            }
            try {
                String idString = jwt.getSubject();
                String username = jwt.getClaim("username").asString();
                String email = jwt.getClaim("email").asString();
                int phoneNumber = jwt.getClaim("phoneNumber").asInt();

                if (idString != null && username != null && email != null && phoneNumber != 0) {
                    // TODO: Check if not too plain
                    if ((request.getServletPath().equals("/profile-completion-form")
                                    || (request.getServletPath().equals("/refresh-token/rotate")))
                            && request.getMethod().equals("POST")) {
                        UUID id = UUID.fromString(idString);
                        User user =
                                userRepository
                                        .findById(id)
                                        .orElseThrow(
                                                () ->
                                                        new BusinessException(
                                                                BusinessExceptionReason
                                                                        .USER_NOT_FOUND));

                        JwtPrincipal principal =
                                new JwtPrincipal(
                                        user, id, username, email, phoneNumber, null, null);
                        AuthenticationToken authentication = new AuthenticationToken(principal);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    String firstName = jwt.getClaim("firstName").asString();
                    String lastName = jwt.getClaim("lastName").asString();
                    if (firstName == null || lastName == null) {
                        throw new BusinessException(BusinessExceptionReason.PROFILE_INCOMPLETE);
                    }
                    UUID id = UUID.fromString(idString);
                    User user = userRepository.findById(id).get();

                    JwtPrincipal principal =
                            new JwtPrincipal(
                                    user, id, username, email, phoneNumber, firstName, lastName);
                    AuthenticationToken authentication = new AuthenticationToken(principal);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (BusinessException e) {
                response.getWriter()
                        .write(
                                objectMapper.writeValueAsString(
                                        new ErrorAccessTokenResponseDto(
                                                e.getCode(),
                                                e.getMessage(),
                                                LocalDateTime.now(),
                                                null)));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
