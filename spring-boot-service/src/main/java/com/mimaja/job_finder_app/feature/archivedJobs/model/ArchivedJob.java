package com.mimaja.job_finder_app.feature.archivedJobs.model;

import com.mimaja.job_finder_app.feature.jobs.locations.model.Location;
import com.mimaja.job_finder_app.feature.jobs.tags.model.Tag;
import com.mimaja.job_finder_app.feature.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "archived_jobs")
public class ArchivedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startTime;

    @ManyToOne()
    private Location location;

    private Double salary;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne()
    @JoinColumn(name = "contractor_id")
    private User contractor;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
