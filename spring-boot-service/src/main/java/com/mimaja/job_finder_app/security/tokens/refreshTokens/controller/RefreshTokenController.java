package com.mimaja.job_finder_app.security.tokens.refreshTokens.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.shared.dto.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh-token")
public class RefreshTokenController {
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;

  @PostMapping("/rotate")
  public ResponseDto<ResponseTokensDto> saveToken(@RequestBody RequestRefreshTokenRotateDto reqData) {
    ResponseTokensDto tokens = refreshTokenServiceDefault.rotateToken(reqData);

    ResponseDto<ResponseTokensDto> response = new ResponseDto<>(
      SuccessCode.RESOURCE_CREATED,
      "Successfully refreshed",
      tokens
    );

    return response;
  }
}
