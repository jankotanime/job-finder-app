package com.mimaja.job_finder_app.security.shared.dto;

import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import java.time.LocalDateTime;
import java.util.List;

public record ErrorAccessTokenResponseDto(
        String code,
        String message,
        LocalDateTime timestamp,
        List<FieldValidationErrorsDto> errors) {}
