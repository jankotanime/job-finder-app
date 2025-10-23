package com.mimaja.job_finder_app.security.authorization.login.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.authorization.login.service.LoginService;
import com.mimaja.job_finder_app.security.shared.dto.RequestLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class LoginController {
  private final LoginService loginService;

  @PostMapping
  public ResponseDto<ResponseTokensDto> loginPostMapping(@RequestBody RequestLoginDto reqData) {
    ResponseTokensDto tokens = loginService.tryToLogin(reqData);

    ResponseDto<ResponseTokensDto> response = new ResponseDto<ResponseTokensDto>(
      SuccessCode.RESOURCE_CREATED,
      "Successfuly logged in",
      tokens
    );

    return response;
  }
}
