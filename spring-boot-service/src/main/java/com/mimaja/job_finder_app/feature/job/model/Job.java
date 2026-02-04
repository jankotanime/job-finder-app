package com.mimaja.job_finder_app.feature.job.model;

import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobphoto.model.JobPhoto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "jobs")
public class Job {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    private String title;

    private String description;

    private LocalDateTime dateAndTime;

    private Double salary;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.READY;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private User contractor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToMany private Set<Tag> tags = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    private JobPhoto photo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "job_dispatcher_id")
    private JobDispatcher jobDispatcher;

    @CreationTimestamp private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public static Job from(Offer offer, JobPhoto photo) {
        Job job = new Job();
        job.title = offer.getTitle();
        job.description = offer.getDescription();
        job.dateAndTime = offer.getDateAndTime();
        job.salary = offer.getSalary();
        job.owner = offer.getOwner();
        job.contractor = offer.getChosenCandidate();
        job.contract = offer.getContract();
        job.photo = photo;
        job.tags = offer.getTags() == null ? new HashSet<>() : new HashSet<>(offer.getTags());
        return job;
    }
}
