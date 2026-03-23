package com.mimaja.job_finder_app.feature.unit.user.profileCompletionForm.service;

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
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.service.FormServiceDefault;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FormServiceDefaultTest {
    private static final String TEST_NEW_FIRST_NAME = "Jane";
    private static final String TEST_NEW_LAST_NAME = "Smith";
    private static final String TEST_NEW_DESCRIPTION = "Updated profile description";
    private static final String TEST_ACCESS_TOKEN = "new-access-token";

    @Mock private UserRepository userRepository;
    @Mock private AccessTokenService accessTokenService;

    private FormServiceDefault formService;
    private User testUser;
    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testPrincipal = JwtPrincipal.from(testUser);
        formService = new FormServiceDefault(userRepository, accessTokenService);
    }

    @Test
    void sendForm_shouldReturnNonNullResponse_whenDataIsValid() {
        setupValidFormMocks();
        ProfileCompletionFormResponseDto result =
                formService.sendForm(createValidRequest(), testPrincipal);
        assertThat(result).isNotNull();
    }

    @Test
    void sendForm_shouldReturnCorrectAccessToken_whenDataIsValid() {
        setupValidFormMocks();
        ProfileCompletionFormResponseDto result =
                formService.sendForm(createValidRequest(), testPrincipal);
        assertThat(result.accessToken()).isEqualTo(TEST_ACCESS_TOKEN);
    }

    @Test
    void sendForm_shouldUpdateFirstName_whenDataIsValid() {
        setupValidFormMocks();
        formService.sendForm(createValidRequest(), testPrincipal);
        assertThat(testUser.getFirstName()).isEqualTo(TEST_NEW_FIRST_NAME);
    }

    @Test
    void sendForm_shouldUpdateLastName_whenDataIsValid() {
        setupValidFormMocks();
        formService.sendForm(createValidRequest(), testPrincipal);
        assertThat(testUser.getLastName()).isEqualTo(TEST_NEW_LAST_NAME);
    }

    @Test
    void sendForm_shouldUpdateProfileDescription_whenDataIsValid() {
        setupValidFormMocks();
        formService.sendForm(createValidRequest(), testPrincipal);
        assertThat(testUser.getProfileDescription()).isEqualTo(TEST_NEW_DESCRIPTION);
    }

    @Test
    void sendForm_shouldCallSaveUser_whenDataIsValid() {
        setupValidFormMocks();
        formService.sendForm(createValidRequest(), testPrincipal);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void sendForm_shouldCallCreateToken_whenDataIsValid() {
        setupValidFormMocks();
        formService.sendForm(createValidRequest(), testPrincipal);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    void sendForm_shouldThrowBusinessException_whenSaveFails() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userRepository)
                .save(testUser);
        assertThrows(
                BusinessException.class,
                () -> formService.sendForm(createValidRequest(), testPrincipal));
    }

    @Test
    void sendForm_shouldNotCallCreateToken_whenSaveFails() {
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userRepository)
                .save(testUser);
        assertThrows(
                BusinessException.class,
                () -> formService.sendForm(createValidRequest(), testPrincipal));
        verify(accessTokenService, never()).createToken(any());
    }

    @Test
    void sendForm_shouldThrowBusinessException_whenTokenCreationFails() {
        when(userRepository.save(testUser)).thenReturn(testUser);
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(accessTokenService)
                .createToken(testUser);
        assertThrows(
                BusinessException.class,
                () -> formService.sendForm(createValidRequest(), testPrincipal));
    }

    @Test
    void sendForm_shouldSetEmptyFirstName_whenFirstNameIsEmpty() {
        setupValidFormMocks();
        formService.sendForm(new ProfileCompletionFormRequestDto("", "", ""), testPrincipal);
        assertThat(testUser.getFirstName()).isEmpty();
    }

    private void setupValidFormMocks() {
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_ACCESS_TOKEN));
    }

    private ProfileCompletionFormRequestDto createValidRequest() {
        return new ProfileCompletionFormRequestDto(
                TEST_NEW_FIRST_NAME, TEST_NEW_LAST_NAME, TEST_NEW_DESCRIPTION);
    }
}
