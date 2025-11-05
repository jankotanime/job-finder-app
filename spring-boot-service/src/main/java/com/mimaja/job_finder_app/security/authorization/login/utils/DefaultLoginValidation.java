package com.mimaja.job_finder_app.security.authorization.login.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultLoginValidation {
    private final UserRepository userRepository;

  public User userValidation(String loginData) {
    Optional<User> userOptional = userRepository.findByUsername(loginData);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginData);
        }

        if (userOptional.isEmpty()) {
            try {
                int phoneNumber = Integer.parseInt(loginData);
                userOptional = userRepository.findByPhoneNumber(phoneNumber);
            } catch (NumberFormatException e) {
                throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
            }
        }

        if (userOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
        }

        return userOptional.get();
    }
}
