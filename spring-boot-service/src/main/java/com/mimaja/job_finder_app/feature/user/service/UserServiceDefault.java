package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.utils.RegisterDataManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceDefault implements UserService {
    private final UserRepository userRepository;
    private final RegisterDataManager registerDataManager;
    private final PasswordConfiguration passwordConfiguration;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User createUser(UserAdminPanelCreateRequestDto dto) {
        registerDataManager.checkRegisterDataDefault(
                dto.username(), dto.email(), dto.phoneNumber(), dto.password());

        String hashedPassword = passwordConfiguration.passwordEncoder().encode(dto.password());

        User user = new User(dto.username(), dto.email(), hashedPassword, null, dto.phoneNumber());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, UserAdminPanelUpdateRequestDto dto) {
        User user = getOrThrow(userId);
        registerDataManager.checkRegisterDataDefault(dto, userId);
        user.update(dto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = getOrThrow(userId);
        userRepository.delete(user);
    }

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
