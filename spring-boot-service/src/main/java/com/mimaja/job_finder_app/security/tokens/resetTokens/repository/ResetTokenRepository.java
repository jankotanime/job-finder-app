package com.mimaja.job_finder_app.security.tokens.resetTokens.repository;

import com.mimaja.job_finder_app.security.tokens.resetTokens.model.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {}
