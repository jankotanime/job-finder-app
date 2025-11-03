package com.mimaja.job_finder_app.security.manage.password.website.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateByEmailDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateEmailRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponsePasswordUpdateEmailRequestDto;

public interface PasswordWebsiteManageService {
  public ResponsePasswordUpdateEmailRequestDto sendEmailWithUpdatePasswordRequest(RequestPasswordUpdateEmailRequestDto reqData);
  public void updatePasswordByEmail(RequestPasswordUpdateByEmailDto reqData);
}
