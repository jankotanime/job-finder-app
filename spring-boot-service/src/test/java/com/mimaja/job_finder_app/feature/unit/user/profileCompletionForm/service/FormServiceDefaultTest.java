package com.mimaja.job_finder_app.feature.unit.user.profileCompletionForm.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUser;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createPrincipal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
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
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String PROFILE_DESCRIPTION = "Updated profile description";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String FALLBACK_ACCESS_TOKEN = "token-123";
    private static final String UPDATED_FIRST_NAME = "UpdatedFirstName";
    private static final String UPDATED_LAST_NAME = "UpdatedLastName";
    private static final String UPDATED_PROFILE_DESCRIPTION = "Updated description";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessTokenService accessTokenService;

    private FormServiceDefault formService;
    private User testUser;
    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createDefaultUser();
        testPrincipal = createPrincipal(testUser);
        formService = new FormServiceDefault(userRepository, accessTokenService);
    }

    @Test
    @DisplayName("Should send form successfully and return access token")
    void shouldReturnAccessToken_WhenSendFormWithValidData() {
        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            FIRST_NAME,
            LAST_NAME,
            PROFILE_DESCRIPTION
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        ProfileCompletionFormResponseDto result = formService.sendForm(requestDto, testPrincipal);

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);

        assertThat(testUser)
            .extracting(User::getFirstName, User::getLastName, User::getProfileDescription)
            .containsExactly(FIRST_NAME, LAST_NAME, PROFILE_DESCRIPTION);

        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should update user first name, last name and profile description correctly")
    void shouldUpdateAllUserFields_WhenSendForm() {
        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            UPDATED_FIRST_NAME,
            UPDATED_LAST_NAME,
            UPDATED_PROFILE_DESCRIPTION
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(FALLBACK_ACCESS_TOKEN);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        formService.sendForm(requestDto, testPrincipal);

        assertThat(testUser)
            .extracting(User::getFirstName, User::getLastName, User::getProfileDescription)
            .containsExactly(UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_PROFILE_DESCRIPTION);

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when user save fails")
    void shouldThrowBusinessException_WhenUserSaveFailsDuringSendForm() {
        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            FIRST_NAME,
            LAST_NAME,
            PROFILE_DESCRIPTION
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
    void shouldThrowBusinessException_WhenTokenCreationFailsDuringSendForm() {
        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            FIRST_NAME,
            LAST_NAME,
            PROFILE_DESCRIPTION
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
    void shouldUpdateUserWithEmptyValues_WhenSendFormWithEmptyStrings() {
        ProfileCompletionFormRequestDto requestDto = new ProfileCompletionFormRequestDto(
            "",
            "",
            ""
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(FALLBACK_ACCESS_TOKEN);

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
