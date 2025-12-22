package com.mimaja.job_finder_app.security.authorization.register.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultRegisterDataManager {
    private final UserRepository userRepository;

    private boolean patternMatches(String data, String regexPattern) {
        return Pattern.compile(regexPattern).matcher(data).matches();
    }

    public void checkDataPatterns(
            String username, String email, String phoneNumberString, String password) {
        if (username.length() < 4 || username.length() > 25) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_LENGTH);
        }

        if (!patternMatches(email, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BusinessException(BusinessExceptionReason.INVALID_EMAIL_PATTERN);
        }

        if (phoneNumberString.length() != 9) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_LENGTH);
        }

        if (password.length() < 8 || password.length() > 128) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_LENGTH);
        }

        if (!patternMatches(password, "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN);
        }
    }

    public void checkUserExistence(String username, String email, int phoneNumber) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
        }

        userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN);
        }

        userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_ALREADY_TAKEN);
        }
    }
}
