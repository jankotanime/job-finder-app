package com.mimaja.job_finder_app.security.configuration;import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfiguration {
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public String encodePassword(String password) {
    return passwordEncoder().encode(password);
  }

  public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder().matches(rawPassword, encodedPassword);
  }
}
