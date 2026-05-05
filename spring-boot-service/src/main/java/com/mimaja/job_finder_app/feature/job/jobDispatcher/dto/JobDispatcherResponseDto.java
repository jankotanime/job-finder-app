package com.mimaja.job_finder_app.feature.job.jobDispatcher.dto;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record JobDispatcherResponseDto(
        UUID jobDispatcherId,
        JobDispatcherIssueStatus issueStatusOwner,
        JobDispatcherIssueStatus issueStatusContractor,
        LocalDateTime finishedAt) {
    public static JobDispatcherResponseDto from(JobDispatcher jobDispatcher) {
        return new JobDispatcherResponseDto(
                jobDispatcher.getId(),
                jobDispatcher.getIssueStatusOwner(),
                jobDispatcher.getIssueStatusContractor(),
                jobDispatcher.getFinishedAt());
    }
}
