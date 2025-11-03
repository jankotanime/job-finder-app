package com.mimaja.job_finder_app.security.manage.password.mobile.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimaja.job_finder_app.security.manage.password.mobile.service.PasswordMobileManageService;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password/mobile")
public class PasswordMobileManageController {
  private final PasswordMobileManageService passwordManageService;

  @PutMapping("/update")
  public ResponseDto<ResponseTokenDto> updatePasswordPostMapping(@RequestBody RequestPasswordDto reqData) {
    passwordManageService.updatePassword(reqData);

    return new ResponseDto<>(SuccessCode.RESOURCE_UPDATED, "Password successfuly updated", null);
  }
}
