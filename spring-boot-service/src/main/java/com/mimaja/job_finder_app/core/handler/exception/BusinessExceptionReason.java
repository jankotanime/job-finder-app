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
    ALREADY_APPLIED_FOR_OFFER("User already applied for this offer", HttpStatus.FORBIDDEN, null),
    APPLICATION_ALREADY_REVIEWED("Application already reviewed", HttpStatus.FORBIDDEN, null),
    APPLICATION_NOT_FOUND("Application not found", HttpStatus.NOT_FOUND, null),
    CANDIDATE_NEED_TO_BE_CHOSEN(
            "Offer doesn't have any chosen candidate", HttpStatus.BAD_REQUEST, null),
    CATEGORY_ALREADY_EXISTS("Category already exists", HttpStatus.CONFLICT, null),
    CATEGORY_NOT_FOUND("Category not found", HttpStatus.NOT_FOUND, null),
    CV_NOT_FOUND("Cv not found", HttpStatus.NOT_FOUND, null),
    EMAIL_ALREADY_TAKEN("User with this email exists", HttpStatus.UNAUTHORIZED, null),
    GOOGLEID_ALREADY_TAKEN("User with this google id exists", HttpStatus.UNAUTHORIZED, null),
    INVALID_ACCESS_TOKEN("Invalid access token", HttpStatus.UNAUTHORIZED, null),
    INVALID_DATA_LENGTH("Invalid data length", HttpStatus.FORBIDDEN, null),
    INVALID_DATA_PATTERN("Invalid data pattern", HttpStatus.FORBIDDEN, null),
    INVALID_EMAIL_PATTERN("Invalid email", HttpStatus.UNAUTHORIZED, null),
    INVALID_GOOGLE_ID("Invalid google id", HttpStatus.UNAUTHORIZED, null),
    INVALID_USERNAME_LENGTH(
            "Username should have between 4 and 25 characters", HttpStatus.UNAUTHORIZED, null),
    INVALID_PASSWORD_LENGTH(
            "Password should have between 8 and 128 characters", HttpStatus.UNAUTHORIZED, null),
    INVALID_PASSWORD_PATTERN("Invalid password", HttpStatus.UNAUTHORIZED, null),
    INVALID_PHONE_NUMBER_LENGTH("Phone number should have 9 digits", HttpStatus.UNAUTHORIZED, null),
    INVALID_PHONE_NUMBER_PATTERN("Invalid phone number", HttpStatus.UNAUTHORIZED, null),
    INVALID_REFRESH_TOKEN("Invalid refresh token", HttpStatus.UNAUTHORIZED, null),
    INVALID_RESET_TOKEN("Invalid reset token", HttpStatus.UNAUTHORIZED, null),
    INVALID_USERNAME_PATTERN("Invalid username", HttpStatus.UNAUTHORIZED, null),
    JOB_NOT_FOUND("Job not found", HttpStatus.NOT_FOUND, null),
    LACK_OF_GOOGLE_ID("Lack of google id", HttpStatus.UNAUTHORIZED, null),
    LACK_OF_PASSWORD("Lack of password", HttpStatus.UNAUTHORIZED, null),
    OFFER_CANDIDATES_LIMIT(
            "Offer has reached maximum number of candidates", HttpStatus.FORBIDDEN, null),
    OFFER_NOT_FOUND("Offer not found", HttpStatus.NOT_FOUND, null),
    OWNER_CANNOT_APPLY("Owner cannot apply for offer", HttpStatus.FORBIDDEN, null),
    PHONE_NUMBER_ALREADY_TAKEN("User with this phone number exists", HttpStatus.UNAUTHORIZED, null),
    PROFILE_INCOMPLETE("User profile needs to be completed", HttpStatus.UNAUTHORIZED, null),
    TAG_ALREADY_EXISTS("Tag already exists", HttpStatus.CONFLICT, null),
    TAG_NOT_FOUND("Tag not found", HttpStatus.NOT_FOUND, null),
    INVALID_SMS_CODE("Invalid sms code", HttpStatus.UNAUTHORIZED, null),
    USER_NOT_CONTRACTOR_OR_OWNER(
            "User is neither contractor or owner of job", HttpStatus.FORBIDDEN, null),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND, null),
    USER_NOT_OWNER("User is not owner", HttpStatus.FORBIDDEN, null),
    USERNAME_ALREADY_TAKEN("User with this username exists", HttpStatus.UNAUTHORIZED, null),
    WRONG_CV_FILE_FORMAT("Wrong cv file format", HttpStatus.BAD_REQUEST, null),
    WRONG_GOOGLE_ID("User with this google id does not exist", HttpStatus.UNAUTHORIZED, null),
    WRONG_LOGIN_DATA("User with this login data does not exist", HttpStatus.UNAUTHORIZED, null),
    WRONG_PASSWORD("Wrong password", HttpStatus.UNAUTHORIZED, null),
    WRONG_PHOTO_FORMAT("Wrong photo format", HttpStatus.BAD_REQUEST, null);

    private final String code = name();
    private final String message;
    private final HttpStatus httpStatus;
    private final List<FieldValidationErrorsDto> errors;
}
