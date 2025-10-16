package com.mimaja.job_finder_app.security.authorization.register.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.authorization.register.service.RegisterServiceDefault;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/register")
public class RegisterController {
  private final RegisterServiceDefault registerService;

  @PostMapping
  public ResponseEntity<Map<String, String>> registerPostMapping(@RequestBody Map<String, String> reqData) {
    Map<String, String> result = registerService.tryToRegister(reqData);

    if (result.containsKey("err")) {
      return ResponseEntity.status(401).body(result);
    }

    return ResponseEntity.ok(result);
  }
}
