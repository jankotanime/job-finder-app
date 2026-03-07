package com.mimaja.job_finder_app.feature.unit.user.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserFilterRequestDto;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.service.UserServiceDefault;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.utils.RegisterDataManager;

import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceDefault - Unit Tests")
public class UserServiceDefaultTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RegisterDataManager registerDataManager;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceDefault userService;

    private User testUser;

    void setUp() {
        testUser = createTestUser();

        userService = new UserServiceDefault(
            userRepository,
            userMapper,
            registerDataManager,
            passwordConfiguration
        );
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

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should get all users with pagination")
    void testGetAllUsers_WithValidPageable_ShouldReturnPageOfUsers() {
        setUp();

        UserFilterRequestDto filterDto = new UserFilterRequestDto("testuser", "user@example.com", null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = userService.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of users should not be null");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should get all users with empty result")
    void testGetAllUsers_WithNoUsers_ShouldReturnEmptyPage() {
        setUp();

        UserFilterRequestDto filterDto = new UserFilterRequestDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = userService.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of users should not be null");
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser_WithValidData_ShouldReturnCreatedUser() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

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

        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(createDto);

        assertNotNull(result, "Created user should not be null");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("user@example.com");

        verify(registerDataManager, times(1)).checkRegisterDataAdminPanel(createDto);
        verify(passwordConfiguration, times(1)).passwordEncoder();
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userMapper, times(1)).toEntity(any(UserAdminPanelCreateRequestDto.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_WithValidData_ShouldReturnUpdatedUser() {
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(userId, updateDto);

        assertNotNull(result, "Updated user should not be null");
        assertThat(result.getId()).isEqualTo(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(registerDataManager, times(1)).checkRegisterDataDefault(updateDto, userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating non-existent user")
    void testUpdateUser_WithNonExistentUser_ShouldThrowBusinessException() {
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

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.updateUser(userId, updateDto),
            "Should throw BusinessException when user not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user not found")
            .isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_WithValidUserId_ShouldDeleteUser() {
        setUp();

        UUID userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting non-existent user")
    void testDeleteUser_WithNonExistentUser_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.deleteUser(userId),
            "Should throw BusinessException when user not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user not found")
            .isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void testGetUserById_WithValidUserId_ShouldReturnUser() {
        setUp();

        UUID userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(userId);

        assertNotNull(result, "User should not be null");
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when getting non-existent user by id")
    void testGetUserById_WithNonExistentUser_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.getUserById(userId),
            "Should throw BusinessException when user not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user not found")
            .isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());

        verify(userRepository, times(1)).findById(userId);
    }
}
