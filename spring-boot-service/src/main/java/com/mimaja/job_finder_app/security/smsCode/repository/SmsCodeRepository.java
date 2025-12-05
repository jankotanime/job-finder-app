package com.mimaja.job_finder_app.security.smsCode.repository;

import com.mimaja.job_finder_app.security.smsCode.model.SmsCode;
import org.springframework.data.repository.CrudRepository;

public interface SmsCodeRepository extends CrudRepository<SmsCode, String> {}
