package com.mimaja.job_finder_app.feature.job.jobDispatcher.controller;

import com.mimaja.job_finder_app.feature.job.jobStatusSignal.dto.JobStatusSignalDto;
import com.mimaja.job_finder_app.feature.job.jobStatusSignal.enums.JobStatusSignalType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class JobDispatcherWebSocketController {
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/job-dispatch/{jobDispatcherId}/subscribe")
  public void handleJobDispatcherSubscription(@DestinationVariable UUID jobDispatcherId) {
    JobStatusSignalDto connectionSignal = new JobStatusSignalDto(
      JobStatusSignalType.JOB_START,
      LocalDateTime.now()
    );

    messagingTemplate.convertAndSend(
      "/job-dispatch/" + jobDispatcherId,
      connectionSignal
    );
  }
}