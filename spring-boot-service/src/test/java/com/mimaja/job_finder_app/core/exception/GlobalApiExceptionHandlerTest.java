package com.mimaja.job_finder_app.core.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.ErrorResponseBuilder;
import com.mimaja.job_finder_app.core.handler.exception.GlobalApiExceptionHandler;
import com.mimaja.job_finder_app.core.handler.exception.dto.ErrorResponseDto;
import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
public class GlobalApiExceptionHandlerTest {
    @InjectMocks private GlobalApiExceptionHandler exceptionHandler;
    @Mock private ServletWebRequest servletWebRequest;
    @Mock private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        lenient().when(servletWebRequest.toString()).thenReturn("test-request");
    }

    @Test
    void handleUncaughtException_shouldReturnInternalServerError() {
        Exception exception = new Exception("test");

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleUncaughtException(exception, servletWebRequest);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode(),
                "Should return internal server error status");
    }

    @Test
    void handleCustomUncaughtBusinessException_shouldReturnBusinessError() {
        BusinessException exception =
                createBusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleCustomUncaughtBusinessException(
                        exception, servletWebRequest);

        assertEquals(
                exception.getHttpStatus(),
                response.getStatusCode(),
                "Should return business exception status");
    }

    @Test
    void handleCustomUncaughtApplicationException_shouldReturnInternalServerError() {
        ApplicationException exception =
                new ApplicationException(ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleUncaughtApplicationException(exception, servletWebRequest);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode(),
                "Should return internal server error status");
    }

    @Test
    void handleIllegalArgumentException_shouldReturnNotFound_whenMessageContainsNotFound() {
        IllegalArgumentException exception = new IllegalArgumentException("not found");

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode(),
                "Should return not found status for 'not found' message");
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest_whenOtherErrorMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("other message");

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return bad request status for other error messages");
    }

    @Test
    void handleUncheckedIOException_shouldReturnNotFound_whenMessageContainsNotFound() {
        IOException ioException = new IOException("not found");
        UncheckedIOException uncheckedIOException = new UncheckedIOException(ioException);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleUncheckedIOException(uncheckedIOException);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode(),
                "Should return not found status for 'not found' message");
    }

    @Test
    void handleUncheckedIOException_shouldReturnBadRequest_whenOtherErrorMessage() {
        IOException ioException = new IOException("other message");
        UncheckedIOException uncheckedIOException = new UncheckedIOException(ioException);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleUncheckedIOException(uncheckedIOException);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return bad request status for other error messages");
    }

    @Test
    void handleEntityNotFoundException_shouldReturnNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("test");

        ResponseEntity<Object> response = exceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(
                HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return not found status");
    }

    @Test
    void handleDataIntegrityViolationException_shouldReturnConflict() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("test");

        ResponseEntity<Object> response =
                exceptionHandler.handleDataIntegrityViolationException(exception);

        assertEquals(
                HttpStatus.CONFLICT, response.getStatusCode(), "Should return conflict status");
    }

    @Test
    void handleDataAccessException_shouldReturnInternalServerError() {
        DataAccessException exception = new DataAccessException("test") {};

        ResponseEntity<Object> response = exceptionHandler.handleDataAccessException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode(),
                "Should return internal server error status");
    }

    @Test
    void handlePersistenceException_shouldReturnInternalServerError() {
        PersistenceException exception = new PersistenceException("test");

        ResponseEntity<Object> response = exceptionHandler.handlePersistenceException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode(),
                "Should return internal server error status");
    }

    @Test
    void handleConstraintViolationException_shouldReturnBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException(new HashSet<>());

        ResponseEntity<Object> response =
                exceptionHandler.handleConstraintViolationException(exception, servletWebRequest);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return bad request status");
    }

    @Test
    void handlePropertyReferenceException_shouldReturnBadRequest() {
        PropertyReferenceException exception = mock(PropertyReferenceException.class);
        when(exception.getPropertyName()).thenReturn("invalidProperty");
        when(exception.getMessage()).thenReturn("Invalid property");

        ResponseEntity<Object> response =
                exceptionHandler.handlePropertyReferenceException(exception, servletWebRequest);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return bad request status");
    }

    @Test
    void errorResponseBuilder_shouldBuildErrorResponseWithCodeAndMessage() {
        ErrorResponseDto response = ErrorResponseBuilder.build(TEST_CODE, TEST_MESSAGE);
        assertEquals(TEST_CODE, response.getCode(), "Should set correct error code");
    }

    @Test
    void errorResponseBuilder_shouldBuildErrorResponseWithErrors() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);

        ErrorResponseDto response =
                ErrorResponseBuilder.build(TEST_CODE, TEST_MESSAGE, List.of(error));

        assertEquals(1, response.getErrors().size(), "Should include field validation error");
    }

    @Test
    void businessExceptionConstructorWithReasonOnly_shouldSetCorrectProperties() {
        BusinessException exception =
                createBusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);

        assertEquals(
                BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode(),
                exception.getCode(),
                "Should set correct error code");
    }

    @Test
    void businessExceptionConstructorWithOverridedHttpStatus_shouldUseCustomStatus() {
        HttpStatus customStatus = HttpStatus.BAD_GATEWAY;
        BusinessException exception =
                createBusinessExceptionWithStatus(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, customStatus);

        assertEquals(customStatus, exception.getHttpStatus(), "Should use custom HTTP status");
    }

    @Test
    void businessExceptionConstructorWithParametersNull_shouldHandleNullParameters() {
        BusinessException exception =
                new BusinessException(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, (Object[]) null);

        assertEquals(
                BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode(),
                exception.getCode(),
                "Should handle null parameters");
    }

    @Test
    void businessExceptionConstructorWithErrors_shouldIncludeFieldErrors() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);
        BusinessException exception =
                new BusinessException(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, List.of(error));

        assertEquals(1, exception.getErrors().size(), "Should include field validation errors");
    }

    @Test
    void businessExceptionConstructorWithNullErrors_shouldHandleNullErrors() {
        BusinessException exception =
                new BusinessException(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN,
                        (List<FieldValidationErrorsDto>) null);

        assertEquals(0, exception.getErrors().size(), "Should null errors as empty list");
    }

    @Test
    void businessExceptionGetLocalizedMessage_shouldReturnSameAsMessage() {
        BusinessException exception =
                new BusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);

        assertEquals(
                exception.getMessage(),
                exception.getLocalizedMessage(),
                "Localized message should match regular message ");
    }

    @Test
    void businessExceptionToString_shouldIncludeExceptionDetails() {
        BusinessException exception =
                createBusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);

        String toString = exception.toString();
        assertThat(toString)
                .as("Should contain class name in toString")
                .contains("BusinessException");
    }

    @Test
    void businessExceptionToStringWithErrors_shouldIncludeErrorsInToString() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);
        BusinessException exception =
                createBusinessExceptionWithErrors(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, List.of(error));

        String toString = exception.toString();
        assertThat(toString).as("Should include errors in toString").contains("errors=");
    }

    @Test
    void handleCustomUncaughtBusinessExceptionWithOverriddenHttpStatus_shouldUseCustomStatus() {
        HttpStatus customStatus = HttpStatus.BAD_GATEWAY;
        BusinessException exception =
                createBusinessExceptionWithStatus(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, customStatus);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleCustomUncaughtBusinessException(
                        exception, servletWebRequest);

        assertEquals(customStatus, response.getStatusCode(), "Should use custom HTTP status");
    }

    @Test
    void handleCustomUncaughtBusinessExceptionWithErrors_shouldIncludeErrorsInResponse() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);
        BusinessException exception =
                createBusinessExceptionWithErrors(
                        BusinessExceptionReason.INVALID_REFRESH_TOKEN, List.of(error));

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleCustomUncaughtBusinessException(
                        exception, servletWebRequest);

        assertEquals(
                1, response.getBody().getErrors().size(), "Should include field validation error");
    }

    @Test
    void applicationExceptionConstructorWithReasonOnly_shouldSetCorrectProperties() {
        ApplicationException exception =
                createApplicationException(ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS);

        assertEquals(
                ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS.getCode(),
                exception.getCode(),
                "Should set correct error code");
    }

    @Test
    void applicationExceptionConstructorWithParametersNull_shouldHandleNullParameters() {
        ApplicationException exception =
                createApplicationExceptionWithParams(
                        ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS, (Object[]) null);

        assertEquals(
                ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS.getMessage(),
                exception.getMessage(),
                "Should handle null parameters");
    }

    @Test
    void applicationExceptionGetLocalizedMessage_shouldReturnSameAsMessage() {
        ApplicationException exception =
                createApplicationException(ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS);

        assertEquals(
                exception.getMessage(),
                exception.getLocalizedMessage(),
                "Localized message should match regular message ");
    }

    @Test
    void applicationExceptionConstructorWithParameters_shouldFormatMessageWithParameters() {
        String param1 = "test-param";
        int param2 = 190;
        ApplicationException exception =
                createApplicationExceptionWithParams(
                        ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS, param1, param2);

        assertThat(exception.getMessage())
                .as("Should include parameters in message")
                .contains(param1);
    }

    @Test
    void applicationExceptionToString_shouldIncludeExceptionDetails() {
        ApplicationException exception =
                createApplicationException(ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS);

        String toString = exception.toString();
        assertThat(toString)
                .as("Should include class name in toString")
                .contains("ApplicationException");
    }

    @Test
    void handleCustomUncaughtApplicationExceptionWithParameters_shouldReturnFormattedMessage() {
        String param1 = "test-param";
        int param2 = 190;
        ApplicationException exception =
                createApplicationExceptionWithParams(
                        ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS, param1, param2);

        ResponseEntity<ErrorResponseDto> response =
                exceptionHandler.handleUncaughtApplicationException(exception, servletWebRequest);

        assertThat(response.getBody().getMessage())
                .as("Should include parameters in response message")
                .contains(param1);
    }
}
