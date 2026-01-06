package com.mimaja.job_finder_app.security.token.accessToken.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.utils.AccessTokenSecretKeyManager;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccessTokenServiceDefault implements AccessTokenService {
    private final int lifetimeInMinutes = 5;
    private final AccessTokenSecretKeyManager accessTokenSecretKeyManager;

    public CreateAccessTokenResponseDto createToken(User user) {
        String token =
                JWT.create()
                        .withSubject(user.getId().toString())
                        .withClaim("username", user.getUsername())
                        .withClaim("email", user.getEmail())
                        .withClaim("phoneNumber", user.getPhoneNumber())
                        .withClaim("role", user.getRole().toString())
                        .withClaim("firstName", user.getFirstName())
                        .withClaim("lastName", user.getLastName())
                        .withClaim("profileDescription", user.getProfileDescription())
                        .withClaim(
                                "profilePhoto",
                                user.getProfilePhoto() != null
                                        ? user.getProfilePhoto().getStorageKey()
                                        : "")
                        .withIssuedAt(new Date())
                        .withExpiresAt(
                                new Date(
                                        System.currentTimeMillis()
                                                + (lifetimeInMinutes * 60 * 1000)))
                        .sign(Algorithm.HMAC256(accessTokenSecretKeyManager.getSecretKey()));

        return new CreateAccessTokenResponseDto(token);
    }
}
