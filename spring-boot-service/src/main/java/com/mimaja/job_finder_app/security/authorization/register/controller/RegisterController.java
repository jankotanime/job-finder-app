package com.mimaja.job_finder_app.security.authorization.register.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.authorization.register.service.RegisterServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/register")
public class RegisterController {
  private final RegisterServiceDefault registerService;

  @PostMapping
  public ResponseDto<ResponseTokensDto> registerPostMapping(@RequestBody RequestRegisterDto reqData) {
    ResponseTokensDto tokens = registerService.tryToRegister(reqData);

    ResponseDto<ResponseTokensDto> response = new ResponseDto<ResponseTokensDto>(
      SuccessCode.RESOURCE_CREATED,
      "Successfully registered",
      tokens
    );

    return response;
  }
}
