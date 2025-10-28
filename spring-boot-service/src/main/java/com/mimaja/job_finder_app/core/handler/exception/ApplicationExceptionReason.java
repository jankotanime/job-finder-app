package com.mimaja.job_finder_app.core.handler.exception;import com.mimaja.job_finder_app.core.handler.exception.policy.ApplicationExceptionPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationExceptionReason implements ApplicationExceptionPolicy {
  BEAN_PROPERTY_NOT_EXISTS("Property '%s' for object '%s' doesn't exists");

  private final String code = name();
  private final String message;
}
