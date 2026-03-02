package com.mimaja.job_finder_app.core.exception;

import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.TEST_CODE;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.TEST_ERROR_MESSAGE;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.TEST_FIELD_NAME;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.TEST_MESSAGE;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createApplicationException;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createApplicationExceptionWithParams;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createBusinessException;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createBusinessExceptionWithErrors;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createBusinessExceptionWithStatus;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createFieldValidationError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.core.handler.exception.ErrorCode;
import com.mimaja.job_finder_app.core.handler.exception.ErrorResponseBuilder;
import com.mimaja.job_finder_app.core.handler.exception.GlobalApiExceptionHandler;
import com.mimaja.job_finder_app.core.handler.exception.dto.ErrorResponseDto;
import com.mimaja.job_finder_app.core.handler.exception.dto.FieldValidationErrorsDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(MockitoExtension.class)
public class GlobalApiExceptionHandlerTest {
    private static final BusinessExceptionReason DEFAULT_BUSINESS_REASON =
            BusinessExceptionReason.INVALID_REFRESH_TOKEN;
    private static final ApplicationExceptionReason DEFAULT_APPLICATION_REASON =
            ApplicationExceptionReason.BEAN_PROPERTY_NOT_EXISTS;
    private static final HttpHeaders DEFAULT_HEADERS = HttpHeaders.EMPTY;
    private static final HttpStatus DEFAULT_BAD_REQUEST_STATUS = HttpStatus.BAD_REQUEST;

    @InjectMocks private GlobalApiExceptionHandler exceptionHandler;
    @Mock private ServletWebRequest servletWebRequest;
    @Mock private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        lenient().when(servletWebRequest.toString()).thenReturn("test-request");
    }

    private ErrorResponseDto errorBody(ResponseEntity<?> response) {
        return (ErrorResponseDto) response.getBody();
    }

    private ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException exception) {
        return exceptionHandler.handleCustomUncaughtBusinessException(exception, servletWebRequest);
    }

    private ResponseEntity<ErrorResponseDto> handleApplicationException(
            ApplicationException exception) {
        return exceptionHandler.handleUncaughtApplicationException(exception, servletWebRequest);
    }

    private ResponseEntity<Object> handleBindingException(
            ServletRequestBindingException exception, HttpStatusCode status) {
        return exceptionHandler.handleServletRequestBindingException(
                exception, DEFAULT_HEADERS, status, webRequest);
    }

    private ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception) {
        return exceptionHandler.handleHttpMessageNotReadable(
                exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);
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
        BusinessException exception = createBusinessException(DEFAULT_BUSINESS_REASON);

        ResponseEntity<ErrorResponseDto> response = handleBusinessException(exception);

        assertEquals(
                exception.getHttpStatus(),
                response.getStatusCode(),
                "Should return business exception status");
    }

    @Test
    void handleCustomUncaughtApplicationException_shouldReturnInternalServerError() {
        ApplicationException exception = new ApplicationException(DEFAULT_APPLICATION_REASON);

        ResponseEntity<ErrorResponseDto> response = handleApplicationException(exception);

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
        BusinessException exception = createBusinessException(DEFAULT_BUSINESS_REASON);

        assertEquals(
                DEFAULT_BUSINESS_REASON.getCode(),
                exception.getCode(),
                "Should set correct error code");
    }

    @Test
    void businessExceptionConstructorWithOverridedHttpStatus_shouldUseCustomStatus() {
        HttpStatus customStatus = HttpStatus.BAD_GATEWAY;
        BusinessException exception =
                createBusinessExceptionWithStatus(DEFAULT_BUSINESS_REASON, customStatus);

        assertEquals(customStatus, exception.getHttpStatus(), "Should use custom HTTP status");
    }

    @Test
    void businessExceptionConstructorWithParametersNull_shouldHandleNullParameters() {
        BusinessException exception =
                new BusinessException(DEFAULT_BUSINESS_REASON, (Object[]) null);

        assertEquals(
                DEFAULT_BUSINESS_REASON.getCode(),
                exception.getCode(),
                "Should handle null parameters");
    }

    @Test
    void businessExceptionConstructorWithParameters_shouldUseFormattingBranch() {
        BusinessException exception =
                new BusinessException(DEFAULT_BUSINESS_REASON, "unused-parameter");

        assertEquals(
                DEFAULT_BUSINESS_REASON.getMessage(),
                exception.getMessage(),
                "Should use varargs formatting branch for non-null parameters");
        assertEquals(
                DEFAULT_BUSINESS_REASON.getCode(),
                exception.getCode(),
                "Should keep correct error code");
    }

    @Test
    void businessExceptionConstructorWithErrors_shouldIncludeFieldErrors() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);
        BusinessException exception =
                new BusinessException(DEFAULT_BUSINESS_REASON, List.of(error));

        assertEquals(1, exception.getErrors().size(), "Should include field validation errors");
    }

    @Test
    void businessExceptionConstructorWithNullErrors_shouldHandleNullErrors() {
        BusinessException exception =
                new BusinessException(
                        DEFAULT_BUSINESS_REASON, (List<FieldValidationErrorsDto>) null);

        assertEquals(0, exception.getErrors().size(), "Should null errors as empty list");
    }

    @Test
    void businessExceptionGetLocalizedMessage_shouldReturnSameAsMessage() {
        BusinessException exception = new BusinessException(DEFAULT_BUSINESS_REASON);

        assertEquals(
                exception.getMessage(),
                exception.getLocalizedMessage(),
                "Localized message should match regular message ");
    }

    @Test
    void businessExceptionToString_shouldIncludeExceptionDetails() {
        BusinessException exception = createBusinessException(DEFAULT_BUSINESS_REASON);

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
                createBusinessExceptionWithErrors(DEFAULT_BUSINESS_REASON, List.of(error));

        String toString = exception.toString();
        assertThat(toString).as("Should include errors in toString").contains("errors=");
    }

    @Test
    void handleCustomUncaughtBusinessExceptionWithOverriddenHttpStatus_shouldUseCustomStatus() {
        HttpStatus customStatus = HttpStatus.BAD_GATEWAY;
        BusinessException exception =
                createBusinessExceptionWithStatus(DEFAULT_BUSINESS_REASON, customStatus);

        ResponseEntity<ErrorResponseDto> response = handleBusinessException(exception);

        assertEquals(customStatus, response.getStatusCode(), "Should use custom HTTP status");
    }

    @Test
    void handleCustomUncaughtBusinessExceptionWithErrors_shouldIncludeErrorsInResponse() {
        FieldValidationErrorsDto error =
                createFieldValidationError(TEST_FIELD_NAME, TEST_ERROR_MESSAGE);
        BusinessException exception =
                createBusinessExceptionWithErrors(DEFAULT_BUSINESS_REASON, List.of(error));

        ResponseEntity<ErrorResponseDto> response = handleBusinessException(exception);

        assertEquals(
                1, response.getBody().getErrors().size(), "Should include field validation error");
    }

    @Test
    void applicationExceptionConstructorWithReasonOnly_shouldSetCorrectProperties() {
        ApplicationException exception = createApplicationException(DEFAULT_APPLICATION_REASON);

        assertEquals(
                DEFAULT_APPLICATION_REASON.getCode(),
                exception.getCode(),
                "Should set correct error code");
    }

    @Test
    void applicationExceptionConstructorWithParametersNull_shouldHandleNullParameters() {
        ApplicationException exception =
                createApplicationExceptionWithParams(DEFAULT_APPLICATION_REASON, (Object[]) null);

        assertEquals(
                DEFAULT_APPLICATION_REASON.getMessage(),
                exception.getMessage(),
                "Should handle null parameters");
    }

    @Test
    void applicationExceptionGetLocalizedMessage_shouldReturnSameAsMessage() {
        ApplicationException exception = createApplicationException(DEFAULT_APPLICATION_REASON);

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
                createApplicationExceptionWithParams(DEFAULT_APPLICATION_REASON, param1, param2);

        assertThat(exception.getMessage())
                .as("Should include parameters in message")
                .contains(param1);
    }

    @Test
    void applicationExceptionToString_shouldIncludeExceptionDetails() {
        ApplicationException exception = createApplicationException(DEFAULT_APPLICATION_REASON);

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
                createApplicationExceptionWithParams(DEFAULT_APPLICATION_REASON, param1, param2);

        ResponseEntity<ErrorResponseDto> response = handleApplicationException(exception);

        assertThat(response.getBody().getMessage())
                .as("Should include parameters in response message")
                .contains(param1);
    }

    @Test
    void handleHttpRequestMethodNotSupported_shouldReturnMethodNotAllowed() {
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("PATCH");

        ResponseEntity<Object> response =
                exceptionHandler.handleHttpRequestMethodNotSupported(
                        exception, DEFAULT_HEADERS, HttpStatus.METHOD_NOT_ALLOWED, webRequest);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.METHOD_NOT_ALLOWED.getName(), body.getCode());
    }

    @Test
    void handleHttpMediaTypeNotSupported_shouldReturnBadRequest() {
        HttpMediaTypeNotSupportedException exception =
                new HttpMediaTypeNotSupportedException("Unsupported content type");

        ResponseEntity<Object> response =
                exceptionHandler.handleHttpMediaTypeNotSupported(
                        exception, DEFAULT_HEADERS, HttpStatus.UNSUPPORTED_MEDIA_TYPE, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMaxUploadSizeExceededException_shouldReturnPayloadTooLarge() {
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(10);

        ResponseEntity<Object> response =
                exceptionHandler.handleMaxUploadSizeExceededException(
                        exception, DEFAULT_HEADERS, HttpStatus.PAYLOAD_TOO_LARGE, webRequest);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.MAXIMUM_SIZE_EXCEEDED.getName(), body.getCode());
    }

    @Test
    void handleHttpMediaTypeNotAcceptable_shouldReturnBadRequest() {
        HttpMediaTypeNotAcceptableException exception =
                new HttpMediaTypeNotAcceptableException(List.of());

        ResponseEntity<Object> response =
                exceptionHandler.handleHttpMediaTypeNotAcceptable(
                        exception, DEFAULT_HEADERS, HttpStatus.NOT_ACCEPTABLE, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMissingPathVariable_shouldReturnBadRequest() throws Exception {
        MissingPathVariableException exception =
                new MissingPathVariableException("id", getMethodParameter());

        ResponseEntity<Object> response =
                exceptionHandler.handleMissingPathVariable(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMissingServletRequestParameter_shouldReturnMissingRequestParameterCode() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("page", "String");

        ResponseEntity<Object> response =
                exceptionHandler.handleMissingServletRequestParameter(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.MISSING_REQUEST_PARAMETER.getName(), body.getCode());
    }

    @Test
    void handleMissingServletRequestPart_shouldReturnMissingPartDetails() {
        MissingServletRequestPartException exception =
                new MissingServletRequestPartException("file");

        ResponseEntity<Object> response =
                exceptionHandler.handleMissingServletRequestPart(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.MISSING_REQUEST_PART.getName(), body.getCode());
        assertEquals("file", body.getErrors().getFirst().getField());
    }

    @Test
    void handleServletRequestBindingException_shouldMapMissingHeaderBranch() throws Exception {
        MissingRequestHeaderException exception =
                new MissingRequestHeaderException("Authorization", getMethodParameter());

        ResponseEntity<Object> response =
                handleBindingException(exception, DEFAULT_BAD_REQUEST_STATUS);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Authorization", body.getErrors().getFirst().getField());
        assertThat(body.getErrors().getFirst().getMessage()).contains("Missing header parameter");
    }

    @Test
    void handleServletRequestBindingException_shouldMapMissingQueryBranch() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("sort", "String");

        ResponseEntity<Object> response =
                handleBindingException(exception, DEFAULT_BAD_REQUEST_STATUS);

        ErrorResponseDto body = errorBody(response);
        assertEquals("sort", body.getErrors().getFirst().getField());
        assertThat(body.getErrors().getFirst().getMessage()).contains("Missing query parameter");
    }

    @Test
    void handleServletRequestBindingException_shouldMapMissingPathBranch() throws Exception {
        MissingPathVariableException exception =
                new MissingPathVariableException("offerId", getMethodParameter());

        ResponseEntity<Object> response =
                handleBindingException(exception, DEFAULT_BAD_REQUEST_STATUS);

        ErrorResponseDto body = errorBody(response);
        assertEquals("offerId", body.getErrors().getFirst().getField());
        assertThat(body.getErrors().getFirst().getMessage()).contains("Missing path parameter");
    }

    @Test
    void handleServletRequestBindingException_shouldMapDefaultBranchToUnknown() {
        ServletRequestBindingException exception = new ServletRequestBindingException("unknown");

        ResponseEntity<Object> response =
                handleBindingException(exception, DEFAULT_BAD_REQUEST_STATUS);

        ErrorResponseDto body = errorBody(response);
        assertEquals("unknown", body.getErrors().getFirst().getField());
        assertThat(body.getErrors().getFirst().getMessage()).contains("Missing unknown parameter");
    }

    @Test
    void handleHttpMessageNotReadable_shouldHandleInvalidEnumFormat() {
        InvalidFormatException invalidFormatException =
                InvalidFormatException.from(null, "invalid enum", "WRONG", SampleStatus.class);
        invalidFormatException.prependPath(this, "status");
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Malformed JSON request",
                        invalidFormatException,
                        mock(HttpInputMessage.class));

        ResponseEntity<Object> response = handleHttpMessageNotReadable(exception);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("status", body.getErrors().getFirst().getField());
        assertThat(body.getErrors().getFirst().getMessage()).contains("must be one of:");
        assertThat(body.getErrors().getFirst().getMessage()).contains("ACTIVE");
        assertThat(body.getErrors().getFirst().getMessage()).contains("INACTIVE");
    }

    @Test
    void handleHttpMessageNotReadable_shouldKeepOriginalMessageWhenCauseIsNotInvalidFormat() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("bad payload", mock(HttpInputMessage.class));

        ResponseEntity<Object> response = handleHttpMessageNotReadable(exception);

        ErrorResponseDto body = errorBody(response);
        assertEquals("", body.getErrors().getFirst().getField());
        assertEquals("bad payload", body.getErrors().getFirst().getMessage());
    }

    @Test
    void handleHttpMessageNotReadable_shouldHandleInvalidFormatWithEmptyPathAndNonEnumTarget() {
        InvalidFormatException invalidFormatException =
                InvalidFormatException.from(null, "invalid number", "abc", Integer.class);
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "bad numeric payload",
                        invalidFormatException,
                        mock(HttpInputMessage.class));

        ResponseEntity<Object> response = handleHttpMessageNotReadable(exception);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", body.getErrors().getFirst().getField());
        assertEquals("bad numeric payload", body.getErrors().getFirst().getMessage());
    }

    @Test
    void handleTypeMismatch_shouldUseMethodArgumentNameWhenAvailable() throws Exception {
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "abc", Integer.class, "page", getMethodParameter(), null);

        ResponseEntity<Object> response =
                exceptionHandler.handleTypeMismatch(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        ErrorResponseDto body = errorBody(response);
        assertEquals("page", body.getErrors().getFirst().getField());
        assertEquals("Unexpected type", body.getErrors().getFirst().getMessage());
    }

    @Test
    void handleTypeMismatch_shouldHandleGenericTypeMismatch() {
        TypeMismatchException exception = new TypeMismatchException("abc", Integer.class);

        ResponseEntity<Object> response =
                exceptionHandler.handleTypeMismatch(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        ErrorResponseDto body = errorBody(response);
        assertEquals("Unexpected type specified", body.getMessage());
    }

    @Test
    void handleConversionNotSupported_shouldReturnConversionNotSupportedCode() {
        ConversionNotSupportedException exception =
                new ConversionNotSupportedException("abc", Integer.class, null);

        ResponseEntity<Object> response =
                exceptionHandler.handleConversionNotSupported(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.CONVERSION_NOT_SUPPORTED.getName(), body.getCode());
        assertThat(body.getMessage()).contains("Failed to convert");
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnFieldValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(getMethodParameter(), bindingResult);

        ResponseEntity<Object> response =
                exceptionHandler.handleMethodArgumentNotValid(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email", body.getErrors().getFirst().getField());
        assertEquals("must not be blank", body.getErrors().getFirst().getMessage());
    }

    @Test
    void handleHandlerMethodValidationException_shouldReturnBadRequest() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);
        when(exception.getMessage()).thenReturn("validation failed");

        ResponseEntity<Object> response =
                exceptionHandler.handleHandlerMethodValidationException(
                        exception, DEFAULT_HEADERS, DEFAULT_BAD_REQUEST_STATUS, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals("validation failed", body.getMessage());
    }

    @Test
    void handleNoHandlerFoundException_shouldReturnNotFound() {
        NoHandlerFoundException exception =
                new NoHandlerFoundException("GET", "/missing-endpoint", HttpHeaders.EMPTY);

        ResponseEntity<Object> response =
                exceptionHandler.handleNoHandlerFoundException(
                        exception, DEFAULT_HEADERS, HttpStatus.NOT_FOUND, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.NOT_FOUND.getName(), body.getCode());
    }

    @Test
    void handleNoResourceFoundException_shouldReturnNotFound() {
        NoResourceFoundException exception =
                new NoResourceFoundException(HttpMethod.GET, "/missing-resource");

        ResponseEntity<Object> response =
                exceptionHandler.handleNoResourceFoundException(
                        exception, DEFAULT_HEADERS, HttpStatus.NOT_FOUND, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponseDto body = errorBody(response);
        assertEquals(ErrorCode.NOT_FOUND.getName(), body.getCode());
    }

    @Test
    void handleConstraintViolationException_shouldReturnLastPropertySegment() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        Path.Node firstNode = mock(Path.Node.class);
        Path.Node lastNode = mock(Path.Node.class);

        when(firstNode.toString()).thenReturn("request");
        when(lastNode.toString()).thenReturn("email");
        doAnswer(
                        invocation -> {
                            java.util.function.Consumer<Object> consumer =
                                    invocation.getArgument(0);
                            consumer.accept(firstNode);
                            consumer.accept(lastNode);
                            return null;
                        })
                .when(path)
                .forEach(any());
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        ConstraintViolationException exception =
                new ConstraintViolationException(new HashSet<>(List.of(violation)));

        ResponseEntity<Object> response =
                exceptionHandler.handleConstraintViolationException(exception, servletWebRequest);

        ErrorResponseDto body = errorBody(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email", body.getErrors().getFirst().getField());
        assertEquals("must not be blank", body.getErrors().getFirst().getMessage());
    }

    @Test
    void handlers_shouldWorkWhenErrorLoggingIsDisabled() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalApiExceptionHandler.class);
        Level originalLevel = logger.getLevel();
        logger.setLevel(Level.OFF);

        try {
            BusinessException businessException = createBusinessException(DEFAULT_BUSINESS_REASON);
            assertEquals(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    exceptionHandler
                            .handleUncaughtException(new Exception("x"), servletWebRequest)
                            .getStatusCode());
            assertEquals(
                    businessException.getHttpStatus(),
                    exceptionHandler
                            .handleCustomUncaughtBusinessException(
                                    businessException, servletWebRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    exceptionHandler
                            .handleUncaughtApplicationException(
                                    createApplicationException(DEFAULT_APPLICATION_REASON),
                                    servletWebRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    exceptionHandler
                            .handleIllegalArgumentException(
                                    new IllegalArgumentException("not found"))
                            .getStatusCode());
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    exceptionHandler
                            .handleUncheckedIOException(
                                    new UncheckedIOException(new IOException("not found")))
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleConstraintViolationException(
                                    new ConstraintViolationException(new HashSet<>()),
                                    servletWebRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handlePropertyReferenceException(
                                    mock(PropertyReferenceException.class), servletWebRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleServletRequestBindingException(
                                    new ServletRequestBindingException("x"),
                                    DEFAULT_HEADERS,
                                    DEFAULT_BAD_REQUEST_STATUS,
                                    webRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleHttpMessageNotReadable(
                                    new HttpMessageNotReadableException(
                                            "x", mock(HttpInputMessage.class)),
                                    DEFAULT_HEADERS,
                                    DEFAULT_BAD_REQUEST_STATUS,
                                    webRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleTypeMismatch(
                                    new TypeMismatchException("x", Integer.class),
                                    DEFAULT_HEADERS,
                                    DEFAULT_BAD_REQUEST_STATUS,
                                    webRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleConversionNotSupported(
                                    new ConversionNotSupportedException("x", Integer.class, null),
                                    DEFAULT_HEADERS,
                                    DEFAULT_BAD_REQUEST_STATUS,
                                    webRequest)
                            .getStatusCode());

            BeanPropertyBindingResult bindingResult =
                    new BeanPropertyBindingResult(new Object(), "request");
            bindingResult.addError(new FieldError("request", "field", "invalid"));
            MethodArgumentNotValidException methodArgumentNotValidException =
                    new MethodArgumentNotValidException(getMethodParameter(), bindingResult);
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exceptionHandler
                            .handleMethodArgumentNotValid(
                                    methodArgumentNotValidException,
                                    DEFAULT_HEADERS,
                                    DEFAULT_BAD_REQUEST_STATUS,
                                    webRequest)
                            .getStatusCode());
            assertEquals(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    exceptionHandler
                            .handleDataAccessException(new DataAccessException("x") {})
                            .getStatusCode());
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    private MethodParameter getMethodParameter() throws NoSuchMethodException {
        Method method = TestEndpoint.class.getDeclaredMethod("sample", String.class);
        return new MethodParameter(method, 0);
    }

    private enum SampleStatus {
        ACTIVE,
        INACTIVE
    }

    private static class TestEndpoint {
        @SuppressWarnings("unused")
        public void sample(String value) {}
    }
}
