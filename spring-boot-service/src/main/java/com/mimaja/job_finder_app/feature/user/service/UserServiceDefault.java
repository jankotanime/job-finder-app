package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceDefault implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID userId) {
        return getOrThrow(userId);
    }

    private User getOrThrow(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.USER_NOT_FOUND));
    }
}
