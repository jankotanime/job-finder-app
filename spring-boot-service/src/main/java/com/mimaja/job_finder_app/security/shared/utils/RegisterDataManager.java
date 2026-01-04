package com.mimaja.job_finder_app.security.shared.utils;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.shared.utils.CheckDataValidity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterDataManager {
    private final CheckDataValidity checkDataValidity;

    public void checkRegisterDataGoogle(
            String username, String email, int phoneNumber, String googleId) {
        checkDataValidity.checkEmail(email);
        checkDataValidity.checkPhoneNumber(phoneNumber);
        checkDataValidity.checkUsername(username);
        checkDataValidity.checkGoogleId(googleId);
    }

    public void checkRegisterDataDefault(
            String username, String email, int phoneNumber, String password) {
        checkDataValidity.checkEmail(email);
        checkDataValidity.checkPhoneNumber(phoneNumber);
        checkDataValidity.checkUsername(username);
        checkDataValidity.checkPassword(password);
    }

    public void checkRegisterDataDefault(UserAdminPanelUpdateRequestDto dto, UUID userId) {
        checkDataValidity.checkUsername(userId, dto.username());
        checkDataValidity.checkEmail(userId, dto.email());
        checkDataValidity.checkPhoneNumber(userId, dto.phoneNumber());
        checkDataValidity.checkRestData(dto.firstName());
        checkDataValidity.checkRestData(dto.lastName());
        checkDataValidity.checkRestData(dto.profileDescription());
    }

    public void checkRegisterDataAdminPanel(UserAdminPanelCreateRequestDto dto) {
        checkDataValidity.checkUsername(dto.username());
        checkDataValidity.checkEmail(dto.email());
        checkDataValidity.checkPhoneNumber(dto.phoneNumber());
        checkDataValidity.checkPassword(dto.password());
        checkDataValidity.checkRestData(dto.firstName());
        checkDataValidity.checkRestData(dto.lastName());
        checkDataValidity.checkRestData(dto.profileDescription());
    }
}
