package com.mimaja.job_finder_app.security.tokens.jwt.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter.JwtPrincipal;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtAuthenticationManager {
    private final UserRepository userRepository;

    public User getUserFromAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_ACCESS_TOKEN);
        }

        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        UUID id = principal.getId();
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.INVALID_ACCESS_TOKEN);
        }

        return userOptional.get();
    }

    public String getUsernameFromAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal)) {
            throw new BusinessException(BusinessExceptionReason.INVALID_ACCESS_TOKEN);
        }

        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return principal.getUsername();
    }
}
