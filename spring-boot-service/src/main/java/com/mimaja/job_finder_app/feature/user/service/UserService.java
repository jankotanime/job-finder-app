package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> getAllUsers(Pageable pageable);

    User createUser(UserAdminPanelCreateRequestDto dto);

    User updateUser(UUID userId, UserAdminPanelUpdateRequestDto dto);

    void deleteUser(UUID userId);

    User getUserById(UUID userId);
}
