package com.mimaja.job_finder_app.feature.users.repository;import com.mimaja.job_finder_app.feature.users.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(int phoneNumber);
}
