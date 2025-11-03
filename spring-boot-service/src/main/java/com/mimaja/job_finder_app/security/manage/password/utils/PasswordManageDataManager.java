package com.mimaja.job_finder_app.security.manage.password.utils;import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordManageDataManager {
  private boolean patternMatches(String data, String regexPattern) {
    return Pattern.compile(regexPattern).matcher(data).matches();
  }

  public void checkDataPatterns(String password) {
    if (password.length() < 8 || password.length() > 128) {
      throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_LENGTH);
    }

    if (!patternMatches(password, "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
      throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN);
    }
  }
}
