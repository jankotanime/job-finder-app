package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID userId);
}
