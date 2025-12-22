package com.mimaja.job_finder_app.security.token.refreshToken.repository;

import com.mimaja.job_finder_app.security.token.refreshToken.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}
