package com.mimaja.job_finder_app.security.tokens.refreshTokens.controller;import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh-token")
public class RefreshTokenController {
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;

  @PostMapping("/rotate")
  public ResponseEntity<Map<String, String>> saveToken(@RequestBody Map<String, String> reqData) {
    Map<String, String> response = refreshTokenServiceDefault.rotateToken(reqData);

    return ResponseEntity.ok(response);
  }
}
