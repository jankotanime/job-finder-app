package com.mimaja.job_finder_app.feature.job.jobDispatcher.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_dispatcher")
public class JobDispatcher {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private JobDispatcherIssueStatus issueStatusOwner = JobDispatcherIssueStatus.NONE;

    @Enumerated(EnumType.STRING)
    private JobDispatcherIssueStatus issueStatusContractor = JobDispatcherIssueStatus.NONE;

    @OneToOne(cascade = CascadeType.ALL)
    private ApprovalPhoto ownerApprovalPhoto;

    @OneToOne(cascade = CascadeType.ALL)
    private ApprovalPhoto contractorApprovalPhoto;

    private LocalDateTime finishedAt;
    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
