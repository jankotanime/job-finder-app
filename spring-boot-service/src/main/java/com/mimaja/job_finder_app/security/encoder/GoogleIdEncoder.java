package com.mimaja.job_finder_app.security.encoder;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleIdEncoder {
    @Value("${google.id.secret}")
    private String secretFilePath;

    private String secretKey;

    @PostConstruct
    public void init() throws IOException {
        secretKey = Files.readString(Paths.get(secretFilePath)).trim();
    }

    public String encodeGoogleId(String googleId) {
        return HmacSha256Util.hmacSha256(secretKey, googleId);
    }

    public boolean verifyGoogleId(String rawGoogleId, String hashedGoogleId) {
        return encodeGoogleId(rawGoogleId).equals(hashedGoogleId);
    }
}
