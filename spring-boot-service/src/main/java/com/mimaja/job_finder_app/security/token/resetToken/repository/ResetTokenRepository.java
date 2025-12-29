package com.mimaja.job_finder_app.security.token.resetToken.repository;

import com.mimaja.job_finder_app.security.token.resetToken.model.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {}
