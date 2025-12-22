package com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response;

import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;

public record GoogleAuthLoginResponseDto(ResponseTokenDto tokens, boolean changedEmail) {}
