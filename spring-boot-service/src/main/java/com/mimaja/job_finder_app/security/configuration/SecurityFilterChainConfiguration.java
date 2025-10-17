package com.mimaja.job_finder_app.security.configuration;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter.JwtAuthorizationFilter;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtSecretKeyConfiguration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {
  private final JwtSecretKeyConfiguration jwtSecretKeyConfiguration;

  Boolean jwtAuthorizationEnabled = true; // ! false only for dev

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    String secretKey = jwtSecretKeyConfiguration.getSecretKey();

    if (jwtAuthorizationEnabled) {
      httpSecurity.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.GET, "/health-check").permitAll()
        .requestMatchers(HttpMethod.POST, "/auth/*", "/refresh-token/rotate").permitAll()
        .anyRequest().authenticated());
    } else {
      httpSecurity.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    }
    httpSecurity
      .exceptionHandling(eh -> eh
        .authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(401);
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          Map<String, Object> errorBody = Map.of(
            "err", "Token error"
          );
          new ObjectMapper().writeValue(response.getWriter(), errorBody);
          response.getWriter().flush();
        })
      )
      // ? Web app endpoints need to be protected from csrf
      .csrf(csrf -> csrf
        .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/web"))
      )
      .logout(logout -> logout.disable())
      .formLogin(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .addFilterBefore(new JwtAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }
}
