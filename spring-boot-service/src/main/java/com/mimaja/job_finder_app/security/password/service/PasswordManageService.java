package com.mimaja.job_finder_app.security.password.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;

public interface PasswordManageService {
  public void changePassword(RequestPasswordDto reqData);
}
