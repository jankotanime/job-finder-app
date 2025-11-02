package com.mimaja.job_finder_app.security.tokens.encoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha256Util {
    public static String hmacSha256(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().withoutPadding().encodeToString(rawHmac);

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }
}
