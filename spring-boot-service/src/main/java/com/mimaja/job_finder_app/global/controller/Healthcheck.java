package com.mimaja.job_finder_app.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class Healthcheck {
  @GetMapping
  public ResponseEntity<String> getHealthcheck() {
    return ResponseEntity.ok("Application is running!");
  }
}
