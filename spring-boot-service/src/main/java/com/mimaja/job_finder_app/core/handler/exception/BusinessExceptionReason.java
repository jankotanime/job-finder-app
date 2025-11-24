package com.mimaja.job_finder_app.core.handler.exception;

import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import com.mimaja.job_finder_app.core.handler.exception.policy.BusinessExceptionPolicy;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessExceptionReason implements BusinessExceptionPolicy {
    JOB_NOT_FOUND("Job not found", HttpStatus.NOT_FOUND, null),

    WRONG_GOOGLE_ID("User with this google id does not exist", HttpStatus.UNAUTHORIZED, null),

    WRONG_LOGIN_DATA("User with this login data does not exist", HttpStatus.UNAUTHORIZED, null),
    WRONG_PASSWORD("Wrong password", HttpStatus.UNAUTHORIZED, null),

    INVALID_USERNAME_LENGTH(
            "Username should have between 4 and 25 characters", HttpStatus.UNAUTHORIZED, null),
    INVALID_PASSWORD_LENGTH(
            "Password should have between 8 and 128 characters", HttpStatus.UNAUTHORIZED, null),
    INVALID_PHONE_NUMBER_LENGTH("Phone number should have 9 digits", HttpStatus.UNAUTHORIZED, null),

    INVALID_USERNAME_PATTERN("Invalid username", HttpStatus.UNAUTHORIZED, null),
    INVALID_EMAIL_PATTERN("Invalid email", HttpStatus.UNAUTHORIZED, null),
    INVALID_PHONE_NUMBER_PATTERN("Invalid phone number", HttpStatus.UNAUTHORIZED, null),
    INVALID_PASSWORD_PATTERN("Invalid password", HttpStatus.UNAUTHORIZED, null),

    USERNAME_ALREADY_TAKEN("User with this username exists", HttpStatus.UNAUTHORIZED, null),
    GOOGLEID_ALREADY_TAKEN("User with this google id exists", HttpStatus.UNAUTHORIZED, null),
    EMAIL_ALREADY_TAKEN("User with this email exists", HttpStatus.UNAUTHORIZED, null),
    PHONE_NUMBER_ALREADY_TAKEN("User with this phone number exists", HttpStatus.UNAUTHORIZED, null),

    INVALID_REFRESH_TOKEN("Invalid refresh token", HttpStatus.UNAUTHORIZED, null),
    INVALID_RESET_TOKEN("Invalid reset token", HttpStatus.UNAUTHORIZED, null),
    INVALID_SMS_CODE("Invalid sms code", HttpStatus.UNAUTHORIZED, null),
    INVALID_ACCESS_TOKEN("Inalid access token", HttpStatus.UNAUTHORIZED, null),
    INVALID_GOOGLE_ID("Inalid google id", HttpStatus.UNAUTHORIZED, null);

    private final String code = name();
    private final String message;
    private final HttpStatus httpStatus;
    private final List<FieldValidationErrorsDto> errors;
}
