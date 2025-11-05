package com.mimaja.job_finder_app.security.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter.JwtAuthorizationFilter;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtSecretKeyConfiguration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.profile", havingValue = "dev")
public class SecurityFilterChainDevConfiguration {
    private final JwtSecretKeyConfiguration jwtSecretKeyConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        String secretKey = jwtSecretKeyConfiguration.getSecretKey();

        httpSecurity
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .exceptionHandling(
                        eh ->
                                eh.authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setStatus(401);
                                            response.setContentType("application/json");
                                            response.setCharacterEncoding("UTF-8");
                                            Map<String, Object> errorBody =
                                                    Map.of("err", "Token error");
                                            new ObjectMapper()
                                                    .writeValue(response.getWriter(), errorBody);
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
                        new JwtAuthorizationFilter(secretKey),
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
