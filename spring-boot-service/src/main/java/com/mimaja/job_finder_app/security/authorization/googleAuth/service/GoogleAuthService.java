package com.mimaja.job_finder_app.security.authorization.googleAuth.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;

public interface GoogleAuthService {
    ResponseTokenDto tryToLoginViaGoogle(RequestGoogleAuthDto reqData);
}
