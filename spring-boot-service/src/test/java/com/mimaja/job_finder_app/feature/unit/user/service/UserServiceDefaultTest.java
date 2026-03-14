package com.mimaja.job_finder_app.feature.unit.user.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUser;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final String CREATE_USERNAME = "newuser";
    private static final String CREATE_EMAIL = "newuser@example.com";
    private static final int CREATE_PHONE_NUMBER = 987654321;
    private static final String CREATE_FIRST_NAME = "Jane";
    private static final String CREATE_LAST_NAME = "Smith";
    private static final String CREATE_PROFILE_DESCRIPTION = "New user profile";
    private static final String UPDATE_USERNAME = "updateduser";
    private static final String UPDATE_EMAIL = "updated@example.com";
    private static final int UPDATE_PHONE_NUMBER = 111111111;
    private static final String UPDATE_FIRST_NAME = "Updated";
    private static final String UPDATE_LAST_NAME = "User";
    private static final String UPDATE_PROFILE_DESCRIPTION = "Updated profile";

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

    @BeforeEach
    void setUp() {
        testUser = createDefaultUser();

        userService = new UserServiceDefault(
            userRepository,
            userMapper,
            registerDataManager,
            passwordConfiguration
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should get all users with pagination")
    void shouldReturnPageOfUsers_WhenGetAllUsersWithValidPageable() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(DEFAULT_USERNAME, DEFAULT_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = userService.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of users should not be null");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo(DEFAULT_USERNAME);

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should get all users with empty result")
    void shouldReturnEmptyPage_WhenGetAllUsersWithNoUsers() {
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
    void shouldReturnCreatedUser_WhenCreateUserWithValidData() {
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        UserAdminPanelCreateRequestDto createDto = new UserAdminPanelCreateRequestDto(
            CREATE_USERNAME,
            CREATE_EMAIL,
            CREATE_PHONE_NUMBER,
            RAW_PASSWORD,
            CREATE_FIRST_NAME,
            CREATE_LAST_NAME,
            CREATE_PROFILE_DESCRIPTION
        );

        when(userMapper.toEntity(any(UserAdminPanelCreateRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(createDto);

        assertNotNull(result, "Created user should not be null");
        assertThat(result)
            .extracting(User::getUsername, User::getEmail)
            .containsExactly(DEFAULT_USERNAME, DEFAULT_EMAIL);

        verify(registerDataManager, times(1)).checkRegisterDataAdminPanel(createDto);
        verify(passwordConfiguration, times(1)).passwordEncoder();
        verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
        ArgumentCaptor<UserAdminPanelCreateRequestDto> dtoCaptor =
            ArgumentCaptor.forClass(UserAdminPanelCreateRequestDto.class);
        verify(userMapper, times(1)).toEntity(dtoCaptor.capture());
        UserAdminPanelCreateRequestDto dtoPassedToMapper = dtoCaptor.getValue();
        assertThat(dtoPassedToMapper)
            .extracting(
                UserAdminPanelCreateRequestDto::password,
                UserAdminPanelCreateRequestDto::username,
                UserAdminPanelCreateRequestDto::email
            )
            .containsExactly(ENCODED_PASSWORD, createDto.username(), createDto.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldReturnUpdatedUser_WhenUpdateUserWithValidData() {
        UUID userId = testUser.getId();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            UPDATE_USERNAME,
            UPDATE_EMAIL,
            UPDATE_PHONE_NUMBER,
            UPDATE_FIRST_NAME,
            UPDATE_LAST_NAME,
            UPDATE_PROFILE_DESCRIPTION
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(userId, updateDto);

        assertNotNull(result, "Updated user should not be null");
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result)
            .extracting(
                User::getUsername,
                User::getEmail,
                User::getPhoneNumber,
                User::getFirstName,
                User::getLastName,
                User::getProfileDescription
            )
            .containsExactly(
                UPDATE_USERNAME,
                UPDATE_EMAIL,
                UPDATE_PHONE_NUMBER,
                UPDATE_FIRST_NAME,
                UPDATE_LAST_NAME,
                UPDATE_PROFILE_DESCRIPTION
            );

        verify(userRepository, times(1)).findById(userId);
        verify(registerDataManager, times(1)).checkRegisterDataDefault(updateDto, userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating non-existent user")
    void shouldThrowBusinessException_WhenUpdateUserWithNonExistentUser() {
        UUID userId = UUID.randomUUID();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            UPDATE_USERNAME,
            UPDATE_EMAIL,
            UPDATE_PHONE_NUMBER,
            UPDATE_FIRST_NAME,
            UPDATE_LAST_NAME,
            UPDATE_PROFILE_DESCRIPTION
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
    void shouldDeleteUser_WhenDeleteUserWithValidUserId() {
        UUID userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting non-existent user")
    void shouldThrowBusinessException_WhenDeleteUserWithNonExistentUser() {
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
    void shouldReturnUser_WhenGetUserByIdWithValidUserId() {
        UUID userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(userId);

        assertNotNull(result, "User should not be null");
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo(DEFAULT_USERNAME);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when getting non-existent user by id")
    void shouldThrowBusinessException_WhenGetUserByIdWithNonExistentUser() {
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
