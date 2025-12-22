package com.mimaja.job_finder_app.security.authorization.googleAuth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthCheckExistenceRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthLoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthRegisterRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleAuthLoginResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleIdExistResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.enums.GoogleIdExistence;
import com.mimaja.job_finder_app.security.authorization.googleAuth.utils.GoogleAuthDataManager;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.smsCode.service.SmsCodeServiceDefault;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthServiceDefault implements GoogleAuthService {
    private final UserRepository userRepository;
    private final GoogleAuthDataManager googleAuthDataManager;
    private final RefreshTokenServiceDefault refreshTokenServiceDefault;
    private final GoogleIdTokenVerifier verifier;
    private final SmsCodeServiceDefault smsCodeServiceDefault;

    public GoogleAuthServiceDefault(
            UserRepository userRepository,
            GoogleAuthDataManager googleAuthDataManager,
            RefreshTokenServiceDefault refreshTokenServiceDefault,
            @Value("${google.id}") String googleId,
            SmsCodeServiceDefault smsCodeServiceDefault) {
        this.userRepository = userRepository;
        this.googleAuthDataManager = googleAuthDataManager;
        this.refreshTokenServiceDefault = refreshTokenServiceDefault;
        this.smsCodeServiceDefault = smsCodeServiceDefault;

        this.verifier =
                new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(googleId))
                        .build();
    }

    @Override
    public GoogleAuthLoginResponseDto tryToLoginViaGoogle(GoogleAuthLoginRequestDto reqData) {
        String googleIdToken = reqData.googleToken();

        Boolean changedEmail = false;

        GoogleIdToken verifiedToken;
        try {
            verifiedToken = verifier.verify(googleIdToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        if (verifiedToken == null) {
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        GoogleIdToken.Payload payload = verifiedToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);

        User user;
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new BusinessException(BusinessExceptionReason.WRONG_GOOGLE_ID);
            }
            user = userOptional.get();

            int smsCode = reqData.smsCode();

            if (smsCode == 0) {
                throw new BusinessException(BusinessExceptionReason.LACK_OF_GOOGLE_ID);
            }

            smsCodeServiceDefault.validateCode(user.getId(), smsCode);

            user.setGoogleId(googleId);
            userRepository.save(user);
        } else {
            user = userOptional.get();
        }

        if (user.getEmail() != email) {
            user.setEmail(email);
            changedEmail = true;
        }

        ResponseTokenDto tokens = refreshTokenServiceDefault.createTokensSet(user);
        GoogleAuthLoginResponseDto response = new GoogleAuthLoginResponseDto(tokens, changedEmail);
        return response;
    }

    @Override
    public GoogleIdExistResponseDto checkUserExistence(GoogleAuthCheckExistenceRequestDto reqData) {
        String googleIdToken = reqData.googleToken();

        GoogleIdToken verifiedToken;
        try {
            verifiedToken = verifier.verify(googleIdToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        if (verifiedToken == null) {
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        GoogleIdToken.Payload payload = verifiedToken.getPayload();
        String googleId = payload.getSubject();

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);

        if (userOptional.isPresent()) {
            return new GoogleIdExistResponseDto(GoogleIdExistence.USER_EXIST);
        }

        String email = payload.getEmail();
        Optional<User> userWithSameEmail = userRepository.findByEmail(email);

        if (userWithSameEmail.isPresent()) {
            smsCodeServiceDefault.createCode(userWithSameEmail.get().getId());
            return new GoogleIdExistResponseDto(GoogleIdExistence.USER_EXIST_WITH_EMAIL);
        }

        return new GoogleIdExistResponseDto(GoogleIdExistence.USER_NOT_EXIST);
    }

    @Override
    public ResponseTokenDto tryToRegisterViaGoogle(GoogleAuthRegisterRequestDto reqData) {
        String googleIdToken = reqData.googleToken();
        String username = reqData.username();
        int phoneNumber = reqData.phoneNumber();

        GoogleIdToken verifiedToken;
        try {
            verifiedToken = verifier.verify(googleIdToken);
        } catch (GeneralSecurityException | IOException e) {
            System.out.println(e);
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        if (verifiedToken == null) {
            throw new BusinessException(BusinessExceptionReason.INVALID_GOOGLE_ID);
        }

        GoogleIdToken.Payload payload = verifiedToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();

        User user = googleAuthDataManager.registerUser(username, email, googleId, phoneNumber);

        ResponseTokenDto tokens = refreshTokenServiceDefault.createTokensSet(user);
        return tokens;
    }
}
