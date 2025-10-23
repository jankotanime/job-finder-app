package com.mimaja.job_finder_app.security.authorization.register.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;

public interface RegisterService {
  boolean patternMatches(String emailAddress, String regexPattern);
  public ResponseTokensDto tryToRegister(RequestRegisterDto reqData);
}
