package com.mimaja.job_finder_app.core.exception;import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.createBusinessException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
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
        exceptionHandler.handleCustomUncaughtBusinessException(exception, servletWebRequest);

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

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return not found status");
  }

  @Test
  void handleDataIntegrityViolationException_shouldReturnConflict() {
    DataIntegrityViolationException exception = new DataIntegrityViolationException("test");

    ResponseEntity<Object> response =
        exceptionHandler.handleDataIntegrityViolationException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Should return conflict status");
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
        HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return bad request status");
  }

  @Test
  void handlePropertyReferenceException_shouldReturnBadRequest() {
    PropertyReferenceException exception = mock(PropertyReferenceException.class);
    when(exception.getPropertyName()).thenReturn("invalidProperty");
    when(exception.getMessage()).thenReturn("Invalid property");

    ResponseEntity<Object> response =
        exceptionHandler.handlePropertyReferenceException(exception, servletWebRequest);

    assertEquals(
        HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return bad request status");
  }
}
