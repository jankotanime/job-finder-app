package com.mimaja.job_finder_app.security.shared.dto;

public record RequestRegisterDto(
        String username, String email, String phoneNumber, String password) {}
