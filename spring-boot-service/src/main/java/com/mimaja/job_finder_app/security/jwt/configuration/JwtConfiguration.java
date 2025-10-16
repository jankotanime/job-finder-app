package com.mimaja.job_finder_app.security.jwt.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mimaja.job_finder_app.security.jwt.authorizationFilter.JwtPrincipal;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {
  private final int lifetimeInMinutes = 5;
  private final JwtSecretKeyConfiguration jwtSecretKeyConfiguration;

  public String createToken(UUID id, String username) {
    String token = JWT.create()
      .withSubject(id.toString())
      .withClaim("username", username)
      .withExpiresAt(new Date(System.currentTimeMillis() + (lifetimeInMinutes * 60 * 1000)))
      .sign(Algorithm.HMAC256(jwtSecretKeyConfiguration.getSecretKey()));

    return token;
  }

  public String getUsernameFromJWT() {
    String username = ((JwtPrincipal) SecurityContextHolder.getContext()
      .getAuthentication().getPrincipal()).getUsername();
    return username;
  }

  public String getIdFromJWT() {
    String id = ((JwtPrincipal) SecurityContextHolder.getContext()
      .getAuthentication().getPrincipal()).getId();
    return id;
  }
}
