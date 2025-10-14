package com.mimaja.job_finder_app.feature.users.repository;

import com.mimaja.job_finder_app.feature.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
