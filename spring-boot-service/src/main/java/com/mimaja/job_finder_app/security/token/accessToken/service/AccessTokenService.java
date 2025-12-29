package com.mimaja.job_finder_app.security.token.accessToken.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;

public interface AccessTokenService {
    CreateAccessTokenResponseDto createToken(User user);
}
