package com.mimaja.job_finder_app.core.mockdata;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.ErrorCode;
import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import java.util.List;
import org.springframework.http.HttpStatus;

public class CoreMockData {
    public static final String API_PATH = "/";
    public static final String TEST_ERROR_MESSAGE = "Error Message";
    public static final String TEST_CODE = "TEST_CODE";
    public static final String TEST_FIELD_NAME = "testField";
    public static final String TEST_MESSAGE = "Test Message";

    public static BusinessException createBusinessException(BusinessExceptionReason reason) {
        return new BusinessException(reason);
    }

    public static BusinessException createBusinessExceptionWithStatus(
            BusinessExceptionReason reason, HttpStatus status) {
        return new BusinessException(reason, status);
    }

    public static BusinessException createBusinessExceptionWithErrors(
            BusinessExceptionReason reason, List<FieldValidationErrorsDto> errors) {
        return new BusinessException(reason, errors);
    }

    public static ApplicationException createApplicationException(
            ApplicationExceptionReason reason) {
        return new ApplicationException(reason);
    }

    public static ApplicationException createApplicationExceptionWithParams(
            ApplicationExceptionReason reason, Object... params) {
        return new ApplicationException(reason, params);
    }

    public static FieldValidationErrorsDto createFieldValidationError(
            String field, String message) {
        return FieldValidationErrorsDto.builder()
                .code(ErrorCode.BAD_REQUEST)
                .field(field)
                .message(message)
                .build();
    }
}
