package com.mimaja.job_finder_app.feature.unit.user.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_PHONE;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDateTime;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class UserServiceAdminTest {
    private static final String TEST_NEW_USERNAME = "newuser";
    private static final String TEST_NEW_EMAIL = "newuser@example.com";
    private static final int TEST_NEW_PHONE = 987654321;
    private static final String TEST_RAW_PASSWORD = "rawPassword";

    @Mock private UserService userService;
    @Mock private UserDeletionService userDeletionService;
    @Mock private UserMapper userMapper;

    private UserServiceAdmin userServiceAdmin;
    private User testUser;
    private UserAdminPanelResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testResponseDto = createTestResponseDto();
        userServiceAdmin = new UserServiceAdmin(userService, userDeletionService, userMapper);
    }

    @Test
    void getAllUsers_shouldReturnNonEmptyPage_whenUsersExist() {
        UserFilterRequestDto filterDto =
                new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(filterDto, pageable))
                .thenReturn(new PageImpl<>(List.of(testUser), pageable, 1));
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        Page<UserAdminPanelResponseDto> result = userServiceAdmin.getAllUsers(filterDto, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllUsers_shouldCallGetAllUsers_whenGettingUsers() {
        UserFilterRequestDto filterDto =
                new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(filterDto, pageable))
                .thenReturn(new PageImpl<>(List.of(testUser), pageable, 1));
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.getAllUsers(filterDto, pageable);
        verify(userService, times(1)).getAllUsers(filterDto, pageable);
    }

    @Test
    void getAllUsers_shouldCallMapper_whenMappingUsers() {
        UserFilterRequestDto filterDto =
                new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(filterDto, pageable))
                .thenReturn(new PageImpl<>(List.of(testUser), pageable, 1));
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.getAllUsers(filterDto, pageable);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    void getAllUsers_shouldReturnEmptyPage_whenNoUsersExist() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(filterDto, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));
        Page<UserAdminPanelResponseDto> result = userServiceAdmin.getAllUsers(filterDto, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getAllUsers_shouldNotCallMapper_whenNoUsersExist() {
        UserFilterRequestDto filterDto = new UserFilterRequestDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(filterDto, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));
        userServiceAdmin.getAllUsers(filterDto, pageable);
        verify(userMapper, never()).toUserAdminPanelResponseDto(any());
    }

    @Test
    void createUser_shouldReturnNonNullResponseDto_whenDataIsValid() {
        when(userService.createUser(any())).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        UserAdminPanelResponseDto result = userServiceAdmin.createUser(createValidCreateDto());
        assertThat(result).isNotNull();
    }

    @Test
    void createUser_shouldCallCreateUser_whenCreatingUser() {
        when(userService.createUser(any())).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.createUser(createValidCreateDto());
        verify(userService, times(1)).createUser(any());
    }

    @Test
    void createUser_shouldCallMapper_whenCreatingUser() {
        when(userService.createUser(any())).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.createUser(createValidCreateDto());
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    void createUser_shouldThrowBusinessException_whenUserServiceFails() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .createUser(any());
        assertThrows(
                BusinessException.class, () -> userServiceAdmin.createUser(createValidCreateDto()));
    }

    @Test
    void createUser_shouldNotCallMapper_whenUserServiceFails() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .createUser(any());
        assertThrows(
                BusinessException.class, () -> userServiceAdmin.createUser(createValidCreateDto()));
        verify(userMapper, never()).toUserAdminPanelResponseDto(any());
    }

    @Test
    void updateUser_shouldReturnNonNullResponseDto_whenDataIsValid() {
        UUID userId = testUser.getId();
        when(userService.updateUser(userId, createValidUpdateDto())).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        UserAdminPanelResponseDto result =
                userServiceAdmin.updateUser(userId, createValidUpdateDto());
        assertThat(result).isNotNull();
    }

    @Test
    void updateUser_shouldCallUpdateUser_whenUpdatingUser() {
        UUID userId = testUser.getId();
        UserAdminPanelUpdateRequestDto updateDto = createValidUpdateDto();
        when(userService.updateUser(userId, updateDto)).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.updateUser(userId, updateDto);
        verify(userService, times(1)).updateUser(userId, updateDto);
    }

    @Test
    void updateUser_shouldCallMapper_whenUpdatingUser() {
        UUID userId = testUser.getId();
        UserAdminPanelUpdateRequestDto updateDto = createValidUpdateDto();
        when(userService.updateUser(userId, updateDto)).thenReturn(testUser);
        when(userMapper.toUserAdminPanelResponseDto(testUser)).thenReturn(testResponseDto);
        userServiceAdmin.updateUser(userId, updateDto);
        verify(userMapper, times(1)).toUserAdminPanelResponseDto(testUser);
    }

    @Test
    void updateUser_shouldThrowBusinessException_whenUserServiceFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .updateUser(any(), any());
        assertThrows(
                BusinessException.class,
                () -> userServiceAdmin.updateUser(userId, createValidUpdateDto()));
    }

    @Test
    void updateUser_shouldNotCallMapper_whenUserServiceFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .updateUser(any(), any());
        assertThrows(
                BusinessException.class,
                () -> userServiceAdmin.updateUser(userId, createValidUpdateDto()));
        verify(userMapper, never()).toUserAdminPanelResponseDto(any());
    }

    @Test
    void deleteUser_shouldCallDeleteUser_whenDeletingUser() {
        UUID userId = testUser.getId();
        userServiceAdmin.deleteUser(userId);
        verify(userDeletionService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldThrowBusinessException_whenDeletionServiceFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userDeletionService)
                .deleteUser(userId);
        assertThrows(BusinessException.class, () -> userServiceAdmin.deleteUser(userId));
    }

    private UserAdminPanelCreateRequestDto createValidCreateDto() {
        return new UserAdminPanelCreateRequestDto(
                TEST_NEW_USERNAME,
                TEST_NEW_EMAIL,
                TEST_NEW_PHONE,
                TEST_RAW_PASSWORD,
                "Jane",
                "Smith",
                "New user profile");
    }

    private UserAdminPanelUpdateRequestDto createValidUpdateDto() {
        return new UserAdminPanelUpdateRequestDto(
                "updateduser",
                "updated@example.com",
                111111111,
                "Updated",
                "User",
                "Updated profile");
    }

    private UserAdminPanelResponseDto createTestResponseDto() {
        return new UserAdminPanelResponseDto(
                UUID.randomUUID(),
                TEST_USERNAME,
                TEST_EMAIL,
                "John",
                "Doe",
                TEST_PHONE,
                "Test admin profile description",
                null,
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
