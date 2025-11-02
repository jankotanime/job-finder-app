package com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter;import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
  private final String secretKey;

  public JwtAuthorizationFilter(String secretKey) {
    this.secretKey = secretKey;
  }

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
        if (idString != null && username != null) {
          UUID id = UUID.fromString(idString);
          JwtPrincipal principal = new JwtPrincipal(id, username);
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(principal, null, null);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (JWTVerificationException e) {
        throw new BusinessException(BusinessExceptionReason.INVALID_ACCESS_TOKEN);
      }
    }
    filterChain.doFilter(request, response);
  }
}
