package com.mimaja.job_finder_app.security.tokens.jwt.shared;

import java.util.UUID;

import com.mimaja.job_finder_app.feature.user.model.User;

// TODO: Add profile photo to princpal
public record JwtPrincipal(
    User user,
    UUID id,
    String username,
    String email,
    int phoneNumber,
    String firstName,
    String lastName
) {}
