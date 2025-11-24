package com.mimaja.job_finder_app.security.authorization.googleAuth.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseGoogleAuthLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseGoogleIdExistDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;

public interface GoogleAuthService {
    ResponseGoogleIdExistDto checkUserExistence(RequestGoogleAuthLoginDto reqData);

    ResponseGoogleAuthLoginDto tryToLoginViaGoogle(RequestGoogleAuthLoginDto reqData);

    ResponseTokenDto tryToRegisterViaGoogle(RequestGoogleAuthRegisterDto reqData);
}
