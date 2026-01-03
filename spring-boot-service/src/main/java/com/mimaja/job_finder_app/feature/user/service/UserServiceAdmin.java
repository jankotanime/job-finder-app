package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserFilterRequestDto;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceAdmin {
    private final UserService userService;
    private final UserDeletionService userDeletionService;
    private final UserMapper userMapper;

    public Page<UserAdminPanelResponseDto> getAllUsers(
            UserFilterRequestDto userFilterRequestDto, Pageable pageable) {
        return userService
                .getAllUsers(userFilterRequestDto, pageable)
                .map(userMapper::toUserAdminPanelResponseDto);
    }

    public UserAdminPanelResponseDto createUser(UserAdminPanelCreateRequestDto dto) {
        return userMapper.toUserAdminPanelResponseDto(userService.createUser(dto));
    }

    public UserAdminPanelResponseDto updateUser(UUID userId, UserAdminPanelUpdateRequestDto dto) {
        return userMapper.toUserAdminPanelResponseDto(userService.updateUser(userId, dto));
    }

    public void deleteUser(UUID userId) {
        userDeletionService.deleteUser(userId);
    }
}
