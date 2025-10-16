package com.mimaja.job_finder_app.security.jwt.authorizationFilter;

import lombok.Getter;

@Getter
public class JwtPrincipal {
  private final String id;
  private final String username;

  public JwtPrincipal(String id, String username) {
    this.id = id;
    this.username = username;
  }
}

