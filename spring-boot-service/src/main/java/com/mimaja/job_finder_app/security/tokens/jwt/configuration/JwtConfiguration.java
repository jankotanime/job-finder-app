package com.mimaja.job_finder_app.security.tokens.jwt.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {
    private final int lifetimeInMinutes = 5;
    private final JwtSecretKeyConfiguration jwtSecretKeyConfiguration;

    public String createToken(User user) {
        String token =
                JWT.create()
                        .withSubject(user.getId().toString())
                        .withClaim("username", user.getUsername())
                        .withClaim("email", user.getEmail())
                        .withClaim("phoneNumber", user.getPhoneNumber())
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
                        .sign(Algorithm.HMAC256(jwtSecretKeyConfiguration.getSecretKey()));

        return token;
    }
}
