package com.mimaja.job_finder_app.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;

@RestController
@RequestMapping("/me")
public class Me {
  @GetMapping
  public ResponseDto<String> getMe() {
    return new ResponseDto<>(
      SuccessCode.RESPONSE_SUCCESSFUL,
      "Me!",
      "Data"
    );
  }
}
