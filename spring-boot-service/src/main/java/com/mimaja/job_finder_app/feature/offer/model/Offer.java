package com.mimaja.job_finder_app.feature.offer.model;

import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private LocalDateTime dateAndTime;

    private Double salary;

    private OfferStatus status;

    private int maxParticipants;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "chosen_candidate_id")
    private User chosenCandidate;

    @ManyToMany private Set<User> candidates = new HashSet<>();

    @ManyToMany private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public void update(Offer updatedOffer, Set<Tag> tags) {
        this.title = updatedOffer.getTitle();
        this.description = updatedOffer.getDescription();
        this.dateAndTime = updatedOffer.getDateAndTime();
        this.salary = updatedOffer.getSalary();
        this.maxParticipants = updatedOffer.getMaxParticipants();
        this.tags = tags;
    }
}
