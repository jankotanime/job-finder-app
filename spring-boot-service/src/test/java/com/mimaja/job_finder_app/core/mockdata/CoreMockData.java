package com.mimaja.job_finder_app.core.mockdata;import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import org.springframework.http.HttpStatus;

public class CoreMockData {
  public static final String API_PATH = "/";
  public static final String TEST_ERROR_MESSAGE = "Error Message";
  public static final String TEST_CODE = "TEST_CODE";
  public static final String TEST_MESSAGE = "Test Message";

  public static BusinessException createBusinessException(BusinessExceptionReason reason) {
    return new BusinessException(reason);
  }

  public static BusinessException createBusinessExceptionWithStatus(
      BusinessExceptionReason reason, HttpStatus status) {
    return new BusinessException(reason, status);
  }

  public static ApplicationException createApplicationException(ApplicationExceptionReason reason) {
    return new ApplicationException(reason);
  }

  public static ApplicationException createApplicationExceptionWithStatus(
      ApplicationExceptionReason reason, HttpStatus status) {
    return new ApplicationException(reason, status);
  }
}
