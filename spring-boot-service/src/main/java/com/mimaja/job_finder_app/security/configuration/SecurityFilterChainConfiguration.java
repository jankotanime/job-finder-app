package com.mimaja.job_finder_app.security.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.accessToken.authorizationFilter.AuthorizationFilter;
import com.mimaja.job_finder_app.security.token.accessToken.utils.AccessTokenSecretKeyManager;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {
    private final AccessTokenSecretKeyManager accessTokenSecretKeyManager;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        String secretKey = accessTokenSecretKeyManager.getSecretKey();
        httpSecurity
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.GET, "/health-check")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/example-error")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/auth/**",
                                                "/refresh-token/rotate",
                                                "/password/website/send-email")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/password/website/update")
                                        .permitAll()
                                        .requestMatchers("/api-docs/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        eh ->
                                eh.authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setStatus(401);
                                            response.setContentType(
                                                    MediaType.APPLICATION_JSON_VALUE);
                                            response.setCharacterEncoding(
                                                    StandardCharsets.UTF_8.name());
                                            response.getWriter().flush();
                                        }))
                .csrf(
                        csrf ->
                                csrf.requireCsrfProtectionMatcher(
                                        request ->
                                                request.getServletPath() != null
                                                        && request.getServletPath()
                                                                .startsWith("/web")))
                .logout(logout -> logout.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        new AuthorizationFilter(secretKey, userRepository, new ObjectMapper()),
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
