package com.mimaja.job_finder_app.security.authorization.login.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;

public interface LoginService {
  public ResponseTokensDto tryToLogin(RequestLoginDto reqData);
}
