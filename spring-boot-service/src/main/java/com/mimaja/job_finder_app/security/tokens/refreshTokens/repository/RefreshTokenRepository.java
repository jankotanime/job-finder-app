package com.mimaja.job_finder_app.security.tokens.refreshTokens.repository;

import org.springframework.data.repository.CrudRepository;

import com.mimaja.job_finder_app.security.tokens.refreshTokens.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}
