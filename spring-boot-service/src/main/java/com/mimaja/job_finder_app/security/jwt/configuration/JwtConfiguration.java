package com.mimaja.job_finder_app.security.jwt.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mimaja.job_finder_app.security.jwt.authorizationFilter.JwtPrincipal;

import java.util.Date;

@Configuration
public class JwtConfiguration {
  JwtSecretKeyConfiguration jwtSecretKeyConfiguration;

  @Autowired
  JwtConfiguration(JwtSecretKeyConfiguration jwtSecretKeyConfiguration) {
    this.jwtSecretKeyConfiguration = jwtSecretKeyConfiguration;
  }
  public String createToken(String username) {
    String token = JWT.create()
      .withClaim("username", username)
      .withExpiresAt(new Date(System.currentTimeMillis() + (5 * 60 * 1000)))
      .sign(Algorithm.HMAC256(jwtSecretKeyConfiguration.getSecretKey()));

    return token;
  }

  public String getUsernameFromJWT() {
    String username = ((JwtPrincipal) SecurityContextHolder.getContext()
      .getAuthentication().getPrincipal()).getUsername();
    return username;
  }
}
