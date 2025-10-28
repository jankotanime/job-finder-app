package com.mimaja.job_finder_app.core.handler.exception.dto;import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
  private String code;
  private String message;
  private LocalDateTime timestamp;
  private List<FieldValidationErrorsDto> errors;
  @Builder.Default private Object data = null;
}
