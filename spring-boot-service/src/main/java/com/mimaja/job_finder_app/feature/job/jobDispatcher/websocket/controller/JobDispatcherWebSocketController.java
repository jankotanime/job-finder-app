package com.mimaja.job_finder_app.feature.job.jobDispatcher.websocket.controller;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.websocket.service.JobDispatcherWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class JobDispatcherWebSocketController {
  private final JobDispatcherWebSocketService jobDispatcherWebSocketService;

  @MessageMapping("/job-dispatch/{jobDispatcherId}/subscribe")
  public void handleJobDispatcherSubscription(@DestinationVariable UUID jobDispatcherId) {
    jobDispatcherWebSocketService.sendConnectionSignal(jobDispatcherId);
  }
}