package com.mimaja.job_finder_app.feature.job.utils;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobStatusSignal.dto.JobStatusSignalDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JobWebSocketSignalsHandler {
  private final SimpMessagingTemplate messagingTemplate;


  public JobStatusSignalDto createSignal(JobDispatcher jobDispatcher) {

    return new JobStatusSignalDto(
      jobDispatcher.getJobStatusSignalType(),
      jobDispatcher.getTimePassed(),
      jobDispatcher.getUpdatedAt()
    );
  }

  public void sendSignalToRoom(JobDispatcher jobDispatcher) {
    messagingTemplate.convertAndSend(
      "/job-dispatch/" + jobDispatcher.getId(),
      createSignal(jobDispatcher)
    );
  }
}
