package com.mimaja.job_finder_app.security.tokens.encoder;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResetTokenEncoder {
    @Value("${reset.token.secret}")
    private String secretFilePath;

    private String secretKey;

    @PostConstruct
    public void init() throws IOException {
        secretKey = Files.readString(Paths.get(secretFilePath)).trim();
    }

    public String encodeToken(String token) {
        return HmacSha256Util.hmacSha256(secretKey, token);
    }

    public boolean verifyToken(String rawToken, String hashedToken) {
        return encodeToken(rawToken).equals(hashedToken);
    }
}
