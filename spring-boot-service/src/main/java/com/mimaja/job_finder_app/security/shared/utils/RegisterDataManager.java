package com.mimaja.job_finder_app.security.shared.utils;

import com.mimaja.job_finder_app.shared.utils.CheckDataValidity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterDataManager {
    private final CheckDataValidity checkDataValidity;

    public void checkRegisterDataGoogle(
            String usernaname, String email, int phoneNumber, String googleId) {
        checkDataValidity.checkEmail(email);
        checkDataValidity.checkPhoneNumber(phoneNumber);
        checkDataValidity.checkUsername(usernaname);
        checkDataValidity.checkGoogleId(googleId);
    }

    public void checkRegisterDataDefault(
            String usernaname, String email, int phoneNumber, String password) {
        checkDataValidity.checkEmail(email);
        checkDataValidity.checkPhoneNumber(phoneNumber);
        checkDataValidity.checkUsername(usernaname);
        checkDataValidity.checkPassword(password);
    }
}
