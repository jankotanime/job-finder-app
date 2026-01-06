package com.mimaja.job_finder_app.feature.job.jobDispatcher.dto;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.ApprovalPhoto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import java.time.LocalDateTime;

public record JobDispatcherResponseDto(
        JobDispatcherIssueStatus issueStatusOwner,
        JobDispatcherIssueStatus issueStatusContractor,
        ApprovalPhoto ownerApprovalPhoto,
        ApprovalPhoto contractorApprovalPhoto,
        LocalDateTime finishedAt) {
    public static JobDispatcherResponseDto from(JobDispatcher jobDispatcher) {
        return new JobDispatcherResponseDto(
                jobDispatcher.getIssueStatusOwner(),
                jobDispatcher.getIssueStatusContractor(),
                jobDispatcher.getOwnerApprovalPhoto(),
                jobDispatcher.getContractorApprovalPhoto(),
                jobDispatcher.getFinishedAt());
    }
}
