package com.mimaja.job_finder_app.security.tokens.refreshTokens.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh-token")
public class RefreshTokenController {
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;

  @PostMapping("/save")
  public ResponseEntity<Map<String, String>> saveToken() {
    Map<String, String> response = refreshTokenServiceDefault.saveToken();

    return ResponseEntity.ok(response);
  }
}
