package com.mimaja.job_finder_app.feature.job.jobStatusSignal.dto;

import java.time.LocalDateTime;

import com.mimaja.job_finder_app.feature.job.jobStatusSignal.enums.JobStatusSignalType;

public record JobStatusSignalDto(
  JobStatusSignalType signalType,
  LocalDateTime time
) {
  public static JobStatusSignalDto from(JobStatusSignalType jobStatusSignalType) {
    return new JobStatusSignalDto(jobStatusSignalType, LocalDateTime.now());
  }
}
