package com.mimaja.job_finder_app.feature.unit.user.mockdata;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import java.util.UUID;

public class UserMockData {
    public static final String TEST_EMAIL = "user@example.com";
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_FIRST_NAME = "John";
    public static final String TEST_LAST_NAME = "Doe";
    public static final String TEST_PROFILE_DESCRIPTION = "Test profile description";
    public static final int TEST_PHONE = 123456789;
    public static final String TEST_OLD_PASSWORD = "oldPassword";
    public static final String TEST_NEW_PASSWORD = "newPassword123";
    public static final String TEST_CURRENT_PASSWORD = "password";
    public static final String TEST_OLD_HASHED_PASSWORD = "old-hashed-password";
    public static final String TEST_NEW_HASHED_PASSWORD = "new-hashed-password";

    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPhoneNumber(TEST_PHONE);
        user.setFirstName(TEST_FIRST_NAME);
        user.setLastName(TEST_LAST_NAME);
        user.setProfileDescription(TEST_PROFILE_DESCRIPTION);
        return user;
    }

    public static User createTestUserWithPassword() {
        User user = createTestUser();
        user.setPasswordHash(TEST_OLD_HASHED_PASSWORD);
        return user;
    }

    public static User createTestUserWithProfilePhoto() {
        User user = createTestUser();
        ProfilePhotoCreateRequestDto photoDto =
                new ProfilePhotoCreateRequestDto("example", MimeType.JPG, 0, "example");
        user.setProfilePhoto(ProfilePhoto.from(photoDto));
        return user;
    }

    public static UserAdminPanelCreateRequestDto createTestUserAdminPanelCreateRequestDto() {
        return new UserAdminPanelCreateRequestDto(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PHONE,
                "password123",
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_PROFILE_DESCRIPTION);
    }
}
