package com.mimaja.job_finder_app.security.tokens.refreshTokens.repository;import com.mimaja.job_finder_app.security.tokens.refreshTokens.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}
