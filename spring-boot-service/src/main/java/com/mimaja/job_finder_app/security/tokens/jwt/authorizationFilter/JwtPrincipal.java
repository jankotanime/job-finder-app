package com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter;import java.util.UUID;
import lombok.Getter;

@Getter
public class JwtPrincipal {
  private final UUID id;
  private final String username;

  public JwtPrincipal(UUID id, String username) {
    this.id = id;
    this.username = username;
  }
}
