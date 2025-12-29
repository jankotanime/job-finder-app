package com.mimaja.job_finder_app.security.authorization.login.service;

import com.mimaja.job_finder_app.security.authorization.login.dto.request.LoginRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;

public interface LoginService {
    TokenResponseDto tryToLogin(LoginRequestDto reqData);
}
