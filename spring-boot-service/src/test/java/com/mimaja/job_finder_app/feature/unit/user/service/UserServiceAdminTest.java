package com.mimaja.job_finder_app.feature.unit.user.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_FIRST_NAME;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_LAST_NAME;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_PHONE_NUMBER;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.DEFAULT_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
    private static final String NEW_USER_USERNAME = "newuser";
    private static final String NEW_USER_EMAIL = "newuser@example.com";
    private static final int NEW_USER_PHONE_NUMBER = 987654321;
    private static final String NEW_USER_PASSWORD = "rawPassword";
    private static final String NEW_USER_FIRST_NAME = "Jane";
    private static final String NEW_USER_LAST_NAME = "Smith";
    private static final String NEW_USER_PROFILE_DESCRIPTION = "New user profile";
    private static final String UPDATED_USERNAME = "updateduser";
    private static final String UPDATED_EMAIL = "updated@example.com";
    private static final int UPDATED_PHONE_NUMBER = 111111111;
    private static final String UPDATED_FIRST_NAME = "Updated";
    private static final String UPDATED_LAST_NAME = "User";
    private static final String UPDATED_PROFILE_DESCRIPTION = "Updated profile";
    private static final String ADMIN_PROFILE_DESCRIPTION = "Test admin profile description";

    @Mock
    private UserService userService;

    @Mock
    private UserDeletionService userDeletionService;

    @Mock
    private UserMapper userMapper;

    private UserServiceAdmin userServiceAdmin;
    private User testUser;
    private UserAdminPanelResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testUser = createDefaultUser();
        testResponseDto = createTestResponseDto();
        userServiceAdmin = new UserServiceAdmin(userService, userDeletionService, userMapper);
    }

    private UserAdminPanelResponseDto createTestResponseDto() {
        return new UserAdminPanelResponseDto(
            UUID.randomUUID(),
            DEFAULT_USERNAME,
            DEFAULT_EMAIL,
            DEFAULT_FIRST_NAME,
            DEFAULT_LAST_NAME,
            DEFAULT_PHONE_NUMBER,
            ADMIN_PROFILE_DESCRIPTION,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should get all users with pagination and map to response DTOs")
    void shouldReturnResponseDtoPage_WhenGetAllUsersWithValidPageable() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(DEFAULT_USERNAME, DEFAULT_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userService.getAllUsers(filterDto, pageable)).thenReturn(expectedPage);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);

        Page<UserAdminPanelResponseDto> result = userServiceAdmin.getAllUsers(filterDto, pageable);

        assertNotNull(result, "Page of response DTOs should not be null");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).username()).isEqualTo(DEFAULT_USERNAME);

        verify(userService, times(1)).getAllUsers(filterDto, pageable);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    @DisplayName("Should get all users with empty result")
    void shouldReturnEmptyPage_WhenGetAllUsersWithNoUsers() {
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
    void shouldReturnResponseDto_WhenCreateUserWithValidData() {
        UserAdminPanelCreateRequestDto createDto = new UserAdminPanelCreateRequestDto(
            NEW_USER_USERNAME,
            NEW_USER_EMAIL,
            NEW_USER_PHONE_NUMBER,
            NEW_USER_PASSWORD,
            NEW_USER_FIRST_NAME,
            NEW_USER_LAST_NAME,
            NEW_USER_PROFILE_DESCRIPTION
        );

        when(userService.createUser(createDto)).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);

        UserAdminPanelResponseDto result = userServiceAdmin.createUser(createDto);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result)
            .extracting(UserAdminPanelResponseDto::username, UserAdminPanelResponseDto::email)
            .containsExactly(DEFAULT_USERNAME, DEFAULT_EMAIL);

        verify(userService, times(1)).createUser(createDto);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when user creation fails")
    void shouldThrowBusinessException_WhenCreateUserFailsInUserService() {
        UserAdminPanelCreateRequestDto createDto = new UserAdminPanelCreateRequestDto(
            NEW_USER_USERNAME,
            NEW_USER_EMAIL,
            NEW_USER_PHONE_NUMBER,
            NEW_USER_PASSWORD,
            NEW_USER_FIRST_NAME,
            NEW_USER_LAST_NAME,
            NEW_USER_PROFILE_DESCRIPTION
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
    void shouldReturnResponseDto_WhenUpdateUserWithValidData() {
        UUID userId = testUser.getId();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            UPDATED_USERNAME,
            UPDATED_EMAIL,
            UPDATED_PHONE_NUMBER,
            UPDATED_FIRST_NAME,
            UPDATED_LAST_NAME,
            UPDATED_PROFILE_DESCRIPTION
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
    void shouldThrowBusinessException_WhenUpdateUserFailsInUserService() {
        UUID userId = UUID.randomUUID();
        UserAdminPanelUpdateRequestDto updateDto = new UserAdminPanelUpdateRequestDto(
            UPDATED_USERNAME,
            UPDATED_EMAIL,
            UPDATED_PHONE_NUMBER,
            UPDATED_FIRST_NAME,
            UPDATED_LAST_NAME,
            UPDATED_PROFILE_DESCRIPTION
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
    void shouldDeleteUser_WhenDeleteUserWithValidUserId() {
        UUID userId = testUser.getId();

        userServiceAdmin.deleteUser(userId);

        verify(userDeletionService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user deletion fails")
    void shouldThrowBusinessException_WhenDeleteUserFailsInDeletionService() {
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
