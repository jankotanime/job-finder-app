package com.mimaja.job_finder_app.feature.user.manage.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CheckDataValidity {
    private final UserRepository userRepository;

    private boolean patternMatches(String data, String regexPattern) {
        return Pattern.compile(regexPattern).matcher(data).matches();
    }

    public void checkUsername(String username) {
        if (username.length() < 4 || username.length() > 25) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_LENGTH);
        }

        if (!patternMatches(username, "^(?=.*[a-zA-Z])[^@]+$")) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_PATTERN);
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
        }
    }
}
