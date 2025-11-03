package com.mimaja.job_finder_app.security.manage.password.mobile.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;

public interface PasswordMobileManageService {
  public void updatePassword(RequestPasswordDto reqData);
}
