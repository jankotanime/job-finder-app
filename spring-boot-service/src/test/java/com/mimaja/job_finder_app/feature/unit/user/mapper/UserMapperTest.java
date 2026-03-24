package com.mimaja.job_finder_app.feature.unit.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithProfilePhoto;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserAdminPanelCreateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_USERNAME;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_EMAIL;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_FIRST_NAME;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_LAST_NAME;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_PHONE;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapperImpl;
import com.mimaja.job_finder_app.feature.user.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper - Unit Tests")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should not be null when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldReturnNonNullUser_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertNotNull(result, "User entity should not be null");
    }

    @Test
    @DisplayName("Should map username correctly when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapUsernameCorrectly_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should map email correctly when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapEmailCorrectly_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should map password as passwordHash when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapPasswordAsPasswordHash_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getPasswordHash()).isEqualTo(requestDto.password());
    }

    @Test
    @DisplayName("Should map firstName correctly when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapFirstNameCorrectly_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getFirstName()).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    @DisplayName("Should map lastName correctly when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapLastNameCorrectly_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getLastName()).isEqualTo(TEST_LAST_NAME);
    }

    @Test
    @DisplayName("Should map phoneNumber correctly when mapping UserAdminPanelCreateRequestDto to User entity")
    void testToEntity_shouldMapPhoneNumberCorrectly_whenValidDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = createTestUserAdminPanelCreateRequestDto();

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertThat(result.getPhoneNumber()).isEqualTo(TEST_PHONE);
    }

    @Test
    @DisplayName("Should return null when mapping null UserAdminPanelCreateRequestDto")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        UserAdminPanelCreateRequestDto requestDto = null;

        // when
        User result = userMapper.toEntity(requestDto);

        // then
        assertNull(result, "Should return null for null input");
    }

    // ==================== toUserInOfferResponseDto Tests ====================

    @Test
    @DisplayName("Should not be null when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldReturnNonNullDto_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertNotNull(result, "UserInOfferResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map username correctly when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldMapUsernameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should map firstName correctly when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldMapFirstNameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.firstName()).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    @DisplayName("Should map lastName correctly when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldMapLastNameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.lastName()).isEqualTo(TEST_LAST_NAME);
    }

    @Test
    @DisplayName("Should map phoneNumber correctly when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldMapPhoneNumberCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.phoneNumber()).isEqualTo(TEST_PHONE);
    }

    @Test
    @DisplayName("Should handle null profilePhoto when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldHandleNullProfilePhoto_whenPhotoIsNull() {
        // given
        User user = createTestUser();
        user.setProfilePhoto(null);

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.profilePhoto()).isNull();
    }

    @Test
    @DisplayName("Should map profilePhoto when mapping User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldMapProfilePhoto_whenPhotoExists() {
        // given
        User user = createTestUserWithProfilePhoto();

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertThat(result.profilePhoto()).isNotNull();
    }

    @Test
    @DisplayName("Should return null when mapping null User to UserInOfferResponseDto")
    void testToUserInOfferResponseDto_shouldReturnNull_whenNullUserProvided() {
        // given
        User user = null;

        // when
        UserInOfferResponseDto result = userMapper.toUserInOfferResponseDto(user);

        // then
        assertNull(result, "Should return null for null input");
    }

    // ==================== toSetOfUserInOfferResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Set of users")
    void testToSetOfUserInOfferResponseDto_shouldReturnNull_whenNullSetProvided() {
        // given
        Set<User> users = null;

        // when
        Set<UserInOfferResponseDto> result = userMapper.toSetOfUserInOfferResponseDto(users);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return empty set when mapping empty set of users")
    void testToSetOfUserInOfferResponseDto_shouldReturnEmptySet_whenEmptySetProvided() {
        // given
        Set<User> users = new HashSet<>();

        // when
        Set<UserInOfferResponseDto> result = userMapper.toSetOfUserInOfferResponseDto(users);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should map single user correctly when mapping set with one user")
    void testToSetOfUserInOfferResponseDto_shouldMapSingleUser_whenSetWithOneUserProvided() {
        // given
        Set<User> users = new HashSet<>();
        users.add(createTestUser());

        // when
        Set<UserInOfferResponseDto> result = userMapper.toSetOfUserInOfferResponseDto(users);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should map multiple users correctly when mapping set with multiple users")
    void testToSetOfUserInOfferResponseDto_shouldMapMultipleUsers_whenSetWithMultipleUsersProvided() {
        // given
        Set<User> users = new HashSet<>();
        users.add(createTestUser());
        users.add(createTestUserWithProfilePhoto());

        // when
        Set<UserInOfferResponseDto> result = userMapper.toSetOfUserInOfferResponseDto(users);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should map user properties correctly in set mapping")
    void testToSetOfUserInOfferResponseDto_shouldMapUserPropertiesCorrectly_whenValidUsersProvided() {
        // given
        Set<User> users = new HashSet<>();
        User testUser = createTestUser();
        users.add(testUser);

        // when
        Set<UserInOfferResponseDto> result = userMapper.toSetOfUserInOfferResponseDto(users);

        // then
        assertThat(result)
                .extracting(UserInOfferResponseDto::username)
                .contains(TEST_USERNAME);
    }

    // ==================== toUserAdminPanelResponseDto Tests ====================

    @Test
    @DisplayName("Should not be null when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldReturnNonNullDto_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertNotNull(result, "UserAdminPanelResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map username correctly when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapUsernameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should map email correctly when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapEmailCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should map firstName correctly when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapFirstNameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.firstName()).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    @DisplayName("Should map lastName correctly when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapLastNameCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.lastName()).isEqualTo(TEST_LAST_NAME);
    }

    @Test
    @DisplayName("Should map phoneNumber correctly when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapPhoneNumberCorrectly_whenValidUserProvided() {
        // given
        User user = createTestUser();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.phoneNumber()).isEqualTo(TEST_PHONE);
    }

    @Test
    @DisplayName("Should handle null profilePhoto when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldHandleNullProfilePhoto_whenPhotoIsNull() {
        // given
        User user = createTestUser();
        user.setProfilePhoto(null);

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.profilePhoto()).isNull();
    }

    @Test
    @DisplayName("Should map profilePhoto when mapping User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldMapProfilePhoto_whenPhotoExists() {
        // given
        User user = createTestUserWithProfilePhoto();

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertThat(result.profilePhoto()).isNotNull();
    }

    @Test
    @DisplayName("Should return null when mapping null User to UserAdminPanelResponseDto")
    void testToUserAdminPanelResponseDto_shouldReturnNull_whenNullUserProvided() {
        // given
        User user = null;

        // when
        UserAdminPanelResponseDto result = userMapper.toUserAdminPanelResponseDto(user);

        // then
        assertNull(result, "Should return null for null input");
    }
}
