package com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response;

import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;

public record GoogleAuthLoginResponseDto(TokenResponseDto tokens, boolean changedEmail) {}
