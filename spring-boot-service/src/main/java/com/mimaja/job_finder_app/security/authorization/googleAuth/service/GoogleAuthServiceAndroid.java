package com.mimaja.job_finder_app.security.authorization.googleAuth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.googleAuth.utils.GoogleAuthDataManager;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthServiceAndroid implements GoogleAuthService {
    private final UserRepository userRepository;
    private final GoogleAuthDataManager googleAuthDataManager;
    private final JwtConfiguration jwtConfiguration;
    private final RefreshTokenServiceDefault refreshTokenServiceDefault;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthServiceAndroid(
            UserRepository userRepository,
            GoogleAuthDataManager googleAuthDataManager,
            JwtConfiguration jwtConfiguration,
            RefreshTokenServiceDefault refreshTokenServiceDefault,
            @Value("${google.id.android}") String googleIdAndroid) {
        this.userRepository = userRepository;
        this.googleAuthDataManager = googleAuthDataManager;
        this.jwtConfiguration = jwtConfiguration;
        this.refreshTokenServiceDefault = refreshTokenServiceDefault;

        this.verifier =
                new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(googleIdAndroid))
                        .build();
    }

    @Override
    public ResponseTokenDto tryToLoginViaGoogle(RequestGoogleAuthDto reqData) {
        String username = reqData.username();
        String googleIdToken = reqData.googleId();

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
            user = googleAuthDataManager.registerUser(username, email, googleId);
        } else {
            user = userOptional.get();
            user = googleAuthDataManager.loginUser(user, googleId);
        }

        UUID userId = user.getId();

        String accessToken = jwtConfiguration.createToken(userId, username);

        ResponseRefreshTokenDto refreshToken = refreshTokenServiceDefault.createToken(user.getId());
        ResponseTokenDto tokens =
                new ResponseTokenDto(
                        accessToken, refreshToken.refreshToken(), refreshToken.refreshTokenId());

        return tokens;
    }
}
