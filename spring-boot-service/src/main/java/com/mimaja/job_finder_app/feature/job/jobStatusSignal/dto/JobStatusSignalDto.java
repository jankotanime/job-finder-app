package com.mimaja.job_finder_app.feature.job.jobStatusSignal.dto;

import java.time.LocalDateTime;

import com.mimaja.job_finder_app.feature.job.jobStatusSignal.enums.JobStatusSignalType;

public record JobStatusSignalDto(
  JobStatusSignalType signalType,
  long timePassedMilisecods,
  LocalDateTime time
) {
  public static JobStatusSignalDto from(JobStatusSignalType jobStatusSignalType, long timePassed) {
    return new JobStatusSignalDto(jobStatusSignalType, timePassed, LocalDateTime.now());
  }
}
