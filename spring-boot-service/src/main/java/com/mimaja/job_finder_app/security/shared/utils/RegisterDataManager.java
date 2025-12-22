package com.mimaja.job_finder_app.security.shared.utils;

import com.mimaja.job_finder_app.shared.utils.CheckDataValidity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterDataManager {
    private final CheckDataValidity checkDataValidity;

    public void checkRegisterData(
            String usernaname, String email, int phoneNumber, String password) {
        checkDataValidity.checkEmail(null, email);
        checkDataValidity.checkPhoneNumber(null, phoneNumber);
        checkDataValidity.checkUsername(null, usernaname);
        // checkDataValidity.checkPassword();
    }

    public void checkPassword(String password) {}
}
