package com.mimaja.job_finder_app.core.handler.exception;

import com.mimaja.job_finder_app.core.handler.exception.policy.ApplicationExceptionPolicy;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import static java.lang.String.format;

@Getter
@Setter
public class ApplicationException extends RuntimeException implements ApplicationExceptionPolicy {
    @Serial private static final long serialVersionUID = 1L;
    private final String code;
    private final String message;

    public ApplicationException(final ApplicationExceptionReason reason) {
        this.code = reason.getCode();
        this.message = reason.getCode();
    }

    public ApplicationException(
            final ApplicationExceptionReason reason, final Object... parameters) {
        if(parameters != null) {
            this.message = format(reason.getMessage(), parameters);
        } else {
            this.message = reason.getMessage();
        }

        this.code = reason.getCode();
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String toString() {
        return format(
                "ApplicationException(code=%s, message=%s)", this.getCode(), this.getMessage());
    }
}
