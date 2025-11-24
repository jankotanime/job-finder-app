package com.mimaja.job_finder_app.security.authorization.googleAuth.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleAuthDataManager {
    private final UserRepository userRepository;

    public User registerUser(String username, String email, String googleId, int phoneNumber) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN);
        }

        if (userRepository.findByGoogleId(googleId).isPresent()) {
            throw new BusinessException(BusinessExceptionReason.WRONG_GOOGLE_ID);
        }

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_ALREADY_TAKEN);
        }

        User user = new User(username, email, null, googleId, phoneNumber);

        userRepository.save(user);

        return user;
    }
}
