package com.mimaja.job_finder_app.security.authorization.googleAuth.utils;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.encoder.GoogleIdEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleAuthDataManager {
    private final UserRepository userRepository;
    private final GoogleIdEncoder googleIdEncoder;

    public User registerUser(String username, String email, String googleId) {
        String hashedGoogleId = googleIdEncoder.encodeGoogleId(googleId);
        User user = new User(username, email, null, hashedGoogleId);

        userRepository.save(user);

        return user;
    }

    public User loginUser(User user, String googleId) {
        String userGoogleId = user.getGoogleId();

        if (userGoogleId == null) {
            String hashedGoogleId = googleIdEncoder.encodeGoogleId(googleId);
            user.setGoogleId(hashedGoogleId);
            userRepository.save(user);
            return user;
        }

        googleIdEncoder.verifyGoogleId(googleId, user.getGoogleId());
        return user;
    }
}
