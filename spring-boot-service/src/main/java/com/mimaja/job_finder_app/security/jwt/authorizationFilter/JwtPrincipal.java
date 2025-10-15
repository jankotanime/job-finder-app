package com.mimaja.job_finder_app.security.jwt.authorizationFilter;

import lombok.Getter;

@Getter
public class JwtPrincipal {
  private final String username;

  public JwtPrincipal(String username) {
    this.username = username;
  }
}

