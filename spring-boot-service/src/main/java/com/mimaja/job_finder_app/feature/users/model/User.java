package com.mimaja.job_finder_app.feature.users.model;

import com.mimaja.job_finder_app.feature.archivedJobs.model.ArchivedJob;
import com.mimaja.job_finder_app.feature.jobs.model.Job;
import com.mimaja.job_finder_app.feature.users.ratings.model.Rating;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String email;

    @Nullable private String passwordHash;

    @Nullable private String googleId;

    @Nullable private int phoneNumber;

    @Column(columnDefinition = "TEXT", length = 500)
    private String profileDescription;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobsAsOwner = new ArrayList<>();

    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobsAsContractor = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArchivedJob> archivedJobsAsOwner = new ArrayList<>();

    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArchivedJob> archivedJobsAsContractor = new ArrayList<>();

    private Double balance = 0.00;

    private Double rating;

    @OneToMany(
            mappedBy = "author",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Rating> ratingsGiven;

    @OneToMany(
            mappedBy = "receiver",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Rating> ratingsReceived;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public User(
            String username, String email, String passwordHash, String googleId, int phoneNumber) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleId = googleId;
        this.phoneNumber = phoneNumber;
    }
}
