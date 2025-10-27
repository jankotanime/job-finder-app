package com.mimaja.job_finder_app.security.authorization.login.controller;import com.mimaja.job_finder_app.security.authorization.login.service.LoginService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class LoginController {
  private final LoginService loginService;

  @PostMapping
  public ResponseEntity<Map<String, String>> loginPostMapping(
      @RequestBody Map<String, String> reqData) {
    Map<String, String> response = new HashMap<String, String>();

    response = loginService.tryToLogin(reqData);
    return ResponseEntity.ok(response);
  }
}
