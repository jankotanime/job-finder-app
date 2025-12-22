package com.mimaja.job_finder_app.shared.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CheckDataValidity {
    private final String usernamePattern = "^(?=.*[a-zA-Z])[^@]+$";
    private final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String restDataPattern = "^(?=.*[a-zA-Z]).+$";

    private final UserRepository userRepository;

    private boolean patternMatches(String data, String regexPattern) {
        return Pattern.compile(regexPattern).matcher(data).matches();
    }

    public void checkUsername(UUID userId, String username) {
        if (username.length() < 4 || username.length() > 25) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_LENGTH);
        }

        if (!patternMatches(username, usernamePattern)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_PATTERN);
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            if (!userId.equals(userOptional.get().getId())) {
                throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
            }
        }
    }

    public void checkEmail(UUID userId, String email) {
        if (!patternMatches(email, emailPattern)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_EMAIL_PATTERN);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            if (!userId.equals(userOptional.get().getId())) {
                throw new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN);
            }
        }
    }

    public void checkPhoneNumber(UUID userId, int phoneNumber) {
        if (String.valueOf(phoneNumber).length() != 9) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_LENGTH);
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            if (!userId.equals(userOptional.get().getId())) {
                throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_ALREADY_TAKEN);
            }
        }
    }

    public void checkRestData(String data) {
        if (data.length() < 1) {
            throw new BusinessException(BusinessExceptionReason.INVALID_DATA_LENGTH);
        }
        if (!patternMatches(data, restDataPattern)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_DATA_PATTERN);
        }
    }
}
