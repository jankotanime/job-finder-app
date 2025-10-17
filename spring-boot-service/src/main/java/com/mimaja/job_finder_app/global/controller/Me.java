package com.mimaja.job_finder_app.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class Me {
  @GetMapping
  public ResponseEntity<String> getMe() {
    return ResponseEntity.ok("Me!");
  }
}
