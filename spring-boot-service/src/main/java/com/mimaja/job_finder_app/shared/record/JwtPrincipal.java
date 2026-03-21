package com.mimaja.job_finder_app.shared.record;

import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;

// TODO: Add profile photo to princpal
public record JwtPrincipal(
        User user,
        UUID id,
        String username,
        String email,
        String role,
        int phoneNumber,
        String firstName,
        String lastName) {
    public static JwtPrincipal from(User user) {
        return new JwtPrincipal(
                user,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName());
    }
}
