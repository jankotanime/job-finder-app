package com.mimaja.job_finder_app.feature.unit.user.profileCompletionForm.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.service.FormServiceDefault;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
@DisplayName("FormServiceDefault - Unit Tests")
public class FormServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessTokenService accessTokenService;

    private FormServiceDefault formService;

    private User testUser;

    private JwtPrincipal testPrincipal;

    void setUp() {
        testUser = createTestUser();
        testPrincipal = createTestPrincipal(testUser);

        formService = new FormServiceDefault(userRepository, accessTokenService);
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

    private JwtPrincipal createTestPrincipal(User user) {
        return JwtPrincipal.from(user);
    }

    @Test
    @DisplayName("Should send form successfully and return access token")
    void testSendForm_WithValidData_ShouldReturnAccessToken() {
        setUp();

        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "Jane",
            "Smith",
            "Updated profile description"
        );

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        ProfileCompletionFormResponseDto result = formService.sendForm(requestDto, testPrincipal);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(newAccessToken);

        assertThat(testUser.getFirstName()).isEqualTo("Jane");
        assertThat(testUser.getLastName()).isEqualTo("Smith");
        assertThat(testUser.getProfileDescription()).isEqualTo("Updated profile description");

        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should update user first name, last name and profile description correctly")
    void testSendForm_ShouldUpdateAllUserFields() {
        setUp();

        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "UpdatedFirstName",
            "UpdatedLastName",
            "Updated description"
        );

        String newAccessToken = "token-123";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        formService.sendForm(requestDto, testPrincipal);

        assertThat(testUser.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(testUser.getLastName()).isEqualTo("UpdatedLastName");
        assertThat(testUser.getProfileDescription()).isEqualTo("Updated description");

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void testSendForm_WhenUserSaveFails_ShouldThrowBusinessException() {
        setUp();

        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "Jane",
            "Smith",
            "Updated profile description"
        );

        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userRepository)
            .save(testUser);

        assertThrows(
            BusinessException.class,
            () -> formService.sendForm(requestDto, testPrincipal),
            "Should throw BusinessException when user save fails"
        );

        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(0)).createToken(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when token creation fails")
    void testSendForm_WhenTokenCreationFails_ShouldThrowBusinessException() {
        setUp();

        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "Jane",
            "Smith",
            "Updated profile description"
        );

        when(userRepository.save(testUser)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(accessTokenService)
            .createToken(testUser);

        assertThrows(
            BusinessException.class,
            () -> formService.sendForm(requestDto, testPrincipal),
            "Should throw BusinessException when token creation fails"
        );

        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should handle empty strings in profile completion form")
    void testSendForm_WithEmptyStrings_ShouldUpdateUserWithEmptyValues() {
        setUp();

        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "",
            "",
            ""
        );

        String newAccessToken = "token-123";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        ProfileCompletionFormResponseDto result = formService.sendForm(requestDto, testPrincipal);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(testUser.getFirstName()).isEmpty();
        assertThat(testUser.getLastName()).isEmpty();
        assertThat(testUser.getProfileDescription()).isEmpty();

        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }
}
