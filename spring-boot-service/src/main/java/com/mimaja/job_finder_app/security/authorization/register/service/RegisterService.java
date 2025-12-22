package com.mimaja.job_finder_app.security.authorization.register.service;

import com.mimaja.job_finder_app.security.authorization.register.dto.request.RegisterRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;

public interface RegisterService {
    TokenResponseDto tryToRegister(RegisterRequestDto reqData);
}
