package com.mimaja.job_finder_app.core.handler.exception;import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
  MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "Missing Request Parameter"),
  MISSING_REQUEST_PART(HttpStatus.BAD_REQUEST, "Missing Request Part"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found"),
  CONVERSION_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "Conversion Not Supported"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
  MAXIMUM_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "Maximum Size Exceeded"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!"),
  CONFLICT(HttpStatus.CONFLICT, "Cannot execute, try again later!");

  private final String name = name();
  private final HttpStatus httpStatus;
  private final String message;
}
