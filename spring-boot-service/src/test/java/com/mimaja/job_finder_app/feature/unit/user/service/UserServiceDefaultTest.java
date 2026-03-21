package com.mimaja.job_finder_app.feature.unit.user.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

@ExtendWith(MockitoExtension.class)
class UserServiceDefaultTest {

    private static final String TEST_NEW_USERNAME = "newuser";
    private static final String TEST_NEW_EMAIL    = "newuser@example.com";
    private static final int    TEST_NEW_PHONE    = 987654321;
    private static final String TEST_RAW_PASSWORD = "rawPassword";

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RegisterDataManager registerDataManager;
    @Mock private PasswordConfiguration passwordConfiguration;
    @Mock private PasswordEncoder passwordEncoder;

    private UserServiceDefault userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        userService = new UserServiceDefault(userRepository, userMapper, registerDataManager, passwordConfiguration);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllUsers_shouldReturnNonEmptyPage_whenUsersExist() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testUser), pageable, 1));
        Page<User> result = userService.getAllUsers(filterDto, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllUsers_shouldCallFindAll_whenGettingUsers() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testUser), pageable, 1));
        userService.getAllUsers(filterDto, pageable);
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllUsers_shouldReturnEmptyPage_whenNoUsersExist() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));
        Page<User> result = userService.getAllUsers(filterDto, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void createUser_shouldReturnNonNullUser_whenDataIsValid() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User result = userService.createUser(createValidCreateDto());
        assertThat(result).isNotNull();
    }

    @Test
    void createUser_shouldCheckRegisterData_whenCreatingUser() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.createUser(createValidCreateDto());
        verify(registerDataManager, times(1)).checkRegisterDataAdminPanel(any(UserAdminPanelCreateRequestDto.class));
    }

    @Test
    void createUser_shouldEncodePassword_whenCreatingUser() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.createUser(createValidCreateDto());
        verify(passwordEncoder, times(1)).encode(TEST_RAW_PASSWORD);
    }

    @Test
    void createUser_shouldSaveUser_whenCreatingUser() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.createUser(createValidCreateDto());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldReturnNonNullUser_whenDataIsValid() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User result = userService.updateUser(userId, createValidUpdateDto());
        assertThat(result).isNotNull();
    }

    @Test
    void updateUser_shouldCallFindById_whenUpdatingUser() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.updateUser(userId, createValidUpdateDto());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_shouldSaveUser_whenUpdatingUser() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.updateUser(userId, createValidUpdateDto());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowExceptionWithUserNotFoundCode_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(userId, createValidUpdateDto()));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());
    }

    @Test
    void updateUser_shouldCallFindById_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.updateUser(userId, createValidUpdateDto()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUser_shouldCallFindById_whenDeletingUser() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        userService.deleteUser(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserFound() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        userService.deleteUser(userId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_shouldThrowExceptionWithUserNotFoundCode_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.deleteUser(userId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());
    }

    @Test
    void deleteUser_shouldCallFindById_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUser_shouldNotCallDelete_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(userId);
        assertThat(result).isNotNull();
    }

    @Test
    void getUserById_shouldCallFindById_whenUserExists() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        userService.getUserById(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_shouldThrowExceptionWithUserNotFoundCode_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserById(userId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());
    }

    @Test
    void getUserById_shouldCallFindById_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    private UserAdminPanelCreateRequestDto createValidCreateDto() {
        return new UserAdminPanelCreateRequestDto(
                TEST_NEW_USERNAME, TEST_NEW_EMAIL, TEST_NEW_PHONE,
                TEST_RAW_PASSWORD, "Jane", "Smith", "New user profile");
    }

    private UserAdminPanelUpdateRequestDto createValidUpdateDto() {
        return new UserAdminPanelUpdateRequestDto(
                "updateduser", "updated@example.com", 111111111,
                "Updated", "User", "Updated profile");
    }
}
