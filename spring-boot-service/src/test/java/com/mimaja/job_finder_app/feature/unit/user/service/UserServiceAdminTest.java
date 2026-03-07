package com.mimaja.job_finder_app.feature.unit.user.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserFilterRequestDto;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserDeletionService;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.feature.user.service.UserServiceAdmin;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceAdmin - Unit Tests")
public class UserServiceAdminTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDeletionService userDeletionService;

    @Mock
    private UserMapper userMapper;

    private UserServiceAdmin userServiceAdmin;

    private User testUser;

    private UserAdminPanelResponseDto testResponseDto;

    void setUp() {
        testUser = createTestUser();
        testResponseDto = createTestResponseDto();

        userServiceAdmin = new UserServiceAdmin(userService, userDeletionService, userMapper);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPhoneNumber(123456789);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setProfileDescription("Test profile description");
        return user;
    }

    private UserAdminPanelResponseDto createTestResponseDto() {
        return new UserAdminPanelResponseDto(
            UUID.randomUUID(),
            "testuser",
            "user@example.com",
            "John",
            "Doe",
            123456789,
            "Test admin profile description",
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );


    }

    @Test
    @DisplayName("Should get all users with pagination and map to response DTOs")
    void testGetAllUsers_WithValidPageable_ShouldReturnPageOfUserAdminPanelResponseDtos() {
        setUp();

        UserFilterRequestDto filterDto = new UserFilterRequestDto("testuser", "user@example.com", null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userService.getAllUsers(filterDto, pageable)).thenReturn(expectedPage);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);

        Page<UserAdminPanelResponseDto> result = userServiceAdmin.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of response DTOs should not be null");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("testuser");

        verify(userService, times(1)).getAllUsers(filterDto, pageable);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    @DisplayName("Should get all users with empty result")
    void testGetAllUsers_WithNoUsers_ShouldReturnEmptyPage() {
        setUp();

        UserFilterRequestDto filterDto = new UserFilterRequestDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(userService.getAllUsers(filterDto, pageable)).thenReturn(expectedPage);

        Page<UserAdminPanelResponseDto> result = userServiceAdmin.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of response DTOs should not be null");
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();

        verify(userService, times(1)).getAllUsers(filterDto, pageable);
        verify(userMapper, times(0)).toUserAdminPanelResponseDto(any());
    }

    @Test
    @DisplayName("Should create user successfully and map to response DTO")
    void testCreateUser_WithValidData_ShouldReturnUserAdminPanelResponseDto() {
        setUp();

        UserAdminPanelCreateRequestDto createDto = new UserAdminPanelCreateRequestDto(
            "newuser",
            "newuser@example.com",
            987654321,
            "rawPassword",
            "Jane",
            "Smith",
            "New user profile"
        );

        when(userService.createUser(createDto)).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);

        UserAdminPanelResponseDto result = userServiceAdmin.createUser(createDto);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("user@example.com");

        verify(userService, times(1)).createUser(createDto);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when user creation fails")
    void testCreateUser_WhenUserServiceThrowsException_ShouldThrowBusinessException() {
        setUp();

        UserAdminPanelCreateRequestDto createDto = new UserAdminPanelCreateRequestDto(
            "newuser",
            "newuser@example.com",
            987654321,
            "rawPassword",
            "Jane",
            "Smith",
            "New user profile"
        );

        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userService)
            .createUser(createDto);

        assertThrows(
            BusinessException.class,
            () -> userServiceAdmin.createUser(createDto),
            "Should throw BusinessException when user creation fails"
        );

        verify(userService, times(1)).createUser(createDto);
        verify(userMapper, times(0)).toUserAdminPanelResponseDto(any());
    }

    @Test
    @DisplayName("Should update user successfully and map to response DTO")
    void testUpdateUser_WithValidData_ShouldReturnUserAdminPanelResponseDto() {
        setUp();

        UUID userId = testUser.getId();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            "updateduser",
            "updated@example.com",
            111111111,
            "Updated",
            "User",
            "Updated profile"
        );

        when(userService.updateUser(userId, updateDto)).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);

        UserAdminPanelResponseDto result = userServiceAdmin.updateUser(userId, updateDto);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.id()).isEqualTo(testResponseDto.id());

        verify(userService, times(1)).updateUser(userId, updateDto);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when user update fails")
    void testUpdateUser_WhenUserServiceThrowsException_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            "updateduser",
            "updated@example.com",
            111111111,
            "Updated",
            "User",
            "Updated profile"
        );

        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userService)
            .updateUser(userId, updateDto);

        assertThrows(
            BusinessException.class,
            () -> userServiceAdmin.updateUser(userId, updateDto),
            "Should throw BusinessException when user update fails"
        );

        verify(userService, times(1)).updateUser(userId, updateDto);
        verify(userMapper, times(0)).toUserAdminPanelResponseDto(any());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_WithValidUserId_ShouldDeleteUser() {
        setUp();

        UUID userId = testUser.getId();

        userServiceAdmin.deleteUser(userId);

        verify(userDeletionService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user deletion fails")
    void testDeleteUser_WhenDeletionServiceThrowsException_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userDeletionService)
            .deleteUser(userId);

        assertThrows(
            BusinessException.class,
            () -> userServiceAdmin.deleteUser(userId),
            "Should throw BusinessException when user deletion fails"
        );

        verify(userDeletionService, times(1)).deleteUser(userId);
    }
}
