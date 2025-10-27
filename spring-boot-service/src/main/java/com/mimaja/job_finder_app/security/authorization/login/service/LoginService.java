package com.mimaja.job_finder_app.security.authorization.login.service;import java.util.Map;

public interface LoginService {
  Map<String, String> tryToLogin(Map<String, String> reqData);
}
