package com.mimaja.job_finder_app.feature.user.update.password.utils;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenServiceDefault;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordWebsiteManager {
    private final UserRepository userRepository;
    private final ResetTokenServiceDefault respTokenServiceDefault;

    @Value("${ssr.url}")
    private String ssrUrl;

    public User findUser(String loginData) {
        Optional<User> userOptional = userRepository.findByUsername(loginData);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginData);
        }

        if (userOptional.isEmpty()) {
            try {
                int phoneNumber = Integer.parseInt(loginData);
                userOptional = userRepository.findByPhoneNumber(phoneNumber);
            } catch (NumberFormatException e) {
                throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
            }
        }

        if (userOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
        }

        return userOptional.get();
    }

    public void sendEmail(UUID userId) {
        ResetTokenResponseDto resetToken = respTokenServiceDefault.createToken(userId);
        // TODO: Email needs update
        String emailContent =
                "Hi\n"
                    + "A request has been received to change the password for your ... account.\n"
                    + "To change password click the link below (you will be redirected to external"
                    + " website):"
                        + ssrUrl
                        + "/update-password?token-id="
                        + resetToken.resetTokenId()
                        + "&token="
                        + resetToken.resetToken()
                        + "\n"
                        + "If you did not initiate this request, please contact is immediatelly at"
                        + " ...";

        System.out.println(emailContent);
    }
}
