package com.mimaja.job_finder_app.feature.job.jobDispatcher.websocket.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.repository.JobRepository;
import com.mimaja.job_finder_app.feature.job.utils.JobWebSocketSignalsHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobDispatcherWebSocketServiceDefault implements JobDispatcherWebSocketService {
  private final JobWebSocketSignalsHandler jobWebSocketSignalsHandler;
  private final JobRepository jobRepository;

  public void sendConnectionSignal(UUID jobDispatcherId) {
    Job job = jobRepository.findByJobDispatcherId(jobDispatcherId).orElseThrow(
      () -> new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND)
    );

    JobDispatcher jobDispatcher = job.getJobDispatcher();

    jobWebSocketSignalsHandler.sendSignalToRoom(jobDispatcher);
  }
}
