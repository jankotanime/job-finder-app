package com.mimaja.job_finder_app.feature.job.jobDispatcher.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Approval {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    private ApprovalPhoto approvalPhoto;

    String description;

    public Approval(ApprovalPhoto photo, String description) {
        approvalPhoto = photo;
        this.description = description;
    }
}
