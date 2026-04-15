package com.mimaja.job_finder_app.feature.job.jobDispatcher.websocket.service;

import java.util.UUID;

public interface JobDispatcherWebSocketService {
  void sendConnectionSignal(UUID jobDispatcherId);
}