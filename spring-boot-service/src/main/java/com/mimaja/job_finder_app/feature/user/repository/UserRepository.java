package com.mimaja.job_finder_app.feature.user.repository;

import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(int phoneNumber);

    Optional<User> findByGoogleId(String googleId);
}
