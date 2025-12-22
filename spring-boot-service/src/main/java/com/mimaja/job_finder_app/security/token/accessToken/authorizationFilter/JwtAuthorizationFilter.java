package com.mimaja.job_finder_app.security.token.accessToken.authorizationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final String secretKey;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            try {
                DecodedJWT jwt =
                        JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(secretKey))
                                .build()
                                .verify(accessToken);
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
                        User user = userRepository.findById(id).get();

                        JwtPrincipal principal =
                                new JwtPrincipal(
                                        user, id, username, email, phoneNumber, null, null);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(principal, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    String firstName = jwt.getClaim("firstName").asString();
                    String lastName = jwt.getClaim("lastName").asString();
                    if (firstName == null || lastName == null) {
                        // TODO: To Business exception
                        Map<String, Object> errorBody = new HashMap<>();
                        errorBody.put("code", BusinessExceptionReason.PROFILE_INCOMPLETE.getCode());
                        errorBody.put(
                                "message", BusinessExceptionReason.PROFILE_INCOMPLETE.getMessage());
                        new ObjectMapper().writeValue(response.getWriter(), errorBody);
                    }
                    UUID id = UUID.fromString(idString);
                    User user = userRepository.findById(id).get();

                    JwtPrincipal principal =
                            new JwtPrincipal(
                                    user, id, username, email, phoneNumber, firstName, lastName);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JWTVerificationException e) {
                // TODO: To Business exception
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("code", BusinessExceptionReason.INVALID_ACCESS_TOKEN.getCode());
                errorBody.put("message", BusinessExceptionReason.INVALID_ACCESS_TOKEN.getMessage());
                new ObjectMapper().writeValue(response.getWriter(), errorBody);
            }
        }
        filterChain.doFilter(request, response);
    }
}
