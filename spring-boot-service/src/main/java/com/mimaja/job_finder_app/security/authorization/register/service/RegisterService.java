package com.mimaja.job_finder_app.security.authorization.register.service;import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;

public interface RegisterService {
  public ResponseTokenDto tryToRegister(RequestRegisterDto reqData);
}
