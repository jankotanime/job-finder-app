package com.mimaja.job_finder_app.global.controller;

import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class Healthcheck {
  @GetMapping
  public ResponseDto<String> getHealthcheck() {
    return new ResponseDto<>(
      SuccessCode.RESPONSE_SUCCESSFUL,
      "Health check",
      "Data"
    );
  }
}
