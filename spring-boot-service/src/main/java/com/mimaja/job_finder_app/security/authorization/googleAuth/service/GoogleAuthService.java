package com.mimaja.job_finder_app.security.authorization.googleAuth.service;

import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthCheckExistenceRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthLoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthRegisterRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleAuthLoginResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleIdExistResponseDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;

public interface GoogleAuthService {
    GoogleIdExistResponseDto checkUserExistence(GoogleAuthCheckExistenceRequestDto reqData);

    GoogleAuthLoginResponseDto tryToLoginViaGoogle(GoogleAuthLoginRequestDto reqData);

    ResponseTokenDto tryToRegisterViaGoogle(GoogleAuthRegisterRequestDto reqData);
}
