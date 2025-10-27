package com.mimaja.job_finder_app.security.authorization.register.service;import java.util.Map;

public interface RegisterService {
  boolean patternMatches(String emailAddress, String regexPattern);

  public Map<String, String> tryToRegister(Map<String, String> reqData);
}
