package com.mimaja.job_finder_app.core.handler.exception;

import com.mimaja.job_finder_app.core.handler.exception.policy.ApplicationExceptionPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionReason implements ApplicationExceptionPolicy {
    BEAN_PROPERTY_NOT_EXISTS("Property '%s' for object '%s' doesn't exists"),
    FILE_NAME_MISSING("File name is missing"),
    CONTENT_TYPE_UNKNOWN("Content-Type is unknown"),
    UNSUPPORTED_FILE_TYPE("Unsupported file type"),
    FILE_UPLOAD_EXCEPTION("File upload to Cloudflare R2 failed");

    private final String code = name();
    private final String message;
}
