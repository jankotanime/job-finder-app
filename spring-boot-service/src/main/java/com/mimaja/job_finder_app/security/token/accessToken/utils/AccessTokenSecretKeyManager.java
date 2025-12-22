package com.mimaja.job_finder_app.security.token.accessToken.utils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenSecretKeyManager {
    @Value("${access.token.secret}")
    private String secretFilePath;

    private String secretKey;

    @PostConstruct
    public void init() throws IOException {
        secretKey = Files.readString(Paths.get(secretFilePath));
    }

    public String getSecretKey() {
        return secretKey;
    }
}
