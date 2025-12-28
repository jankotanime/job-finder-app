package com.mimaja.job_finder_app.feature.application.model;

import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Table(name = "applications")
public class Application {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.SENT;

    @ManyToOne private User candidate;

    @ManyToOne private Offer offer;

    @ManyToOne private Cv chosenCv;

    @CreationTimestamp private LocalDateTime appliedAt;

    @UpdateTimestamp private LocalDateTime updatedAt;
}
