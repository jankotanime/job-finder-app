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
    private final String USERNAME_PATTERN = "^(?=.*[a-zA-Z])[^@]+$";
    private final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String REST_DATA_PATTERN = "^(?=.*[a-zA-Z]).+$";
    private final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    private final UserRepository userRepository;

    private boolean patternMatches(String data, String regexPattern) {
        return Pattern.compile(regexPattern).matcher(data).matches();
    }

    public void validateUsername(String username) {
        if (username.length() < 4 || username.length() > 25) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_LENGTH);
        }

        if (!patternMatches(username, USERNAME_PATTERN)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME_PATTERN);
        }
    }

    public void checkUsername(String username) {
        validateUsername(username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
        }
    }

    public void checkUsername(UUID userId, String username) {
        validateUsername(username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && !userId.equals(userOptional.get().getId())) {
            throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
        }
    }

    public void validateEmail(String email) {
        if (!patternMatches(email, EMAIL_PATTERN)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_EMAIL_PATTERN);
        }
    }

    public void checkEmail(String email) {
        validateEmail(email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN);
        }
    }

    public void checkEmail(UUID userId, String email) {
        validateEmail(email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && !userId.equals(userOptional.get().getId())) {
            throw new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN);
        }
    }

    public void validatePhoneNumber(int phoneNumber) {
        if (String.valueOf(phoneNumber).length() != 9) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_LENGTH);
        }
    }

    public void checkPhoneNumber(int phoneNumber) {
        validatePhoneNumber(phoneNumber);

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_ALREADY_TAKEN);
        }
    }

    public void checkPhoneNumber(UUID userId, int phoneNumber) {
        validatePhoneNumber(phoneNumber);

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent() && !userId.equals(userOptional.get().getId())) {
            throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_ALREADY_TAKEN);
        }
    }

    public void checkGoogleId(String googleId) {
        if (userRepository.findByGoogleId(googleId).isPresent()) {
            throw new BusinessException(BusinessExceptionReason.GOOGLEID_ALREADY_TAKEN);
        }
    }

    public void checkPassword(String password) {
        if (password.length() < 8 || password.length() > 128) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_LENGTH);
        }

        if (!patternMatches(password, PASSWORD_PATTERN)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD_PATTERN);
        }
    }

    public void checkRestData(String data) {
        if (data.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.INVALID_DATA_LENGTH);
        }
        if (!patternMatches(data, REST_DATA_PATTERN)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_DATA_PATTERN);
        }
    }
}
