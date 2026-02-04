package com.mimaja.job_finder_app.feature.offer.model;

import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
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
import jakarta.persistence.OneToMany;
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
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private LocalDateTime dateAndTime;

    private Double salary;

    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.OPEN;

    private int maxApplications;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "chosen_candidate_id")
    private User chosenCandidate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private Set<Application> applications = new HashSet<>();

    @ManyToMany private Set<Tag> tags = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    private OfferPhoto photo;

    @CreationTimestamp private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public void update(Offer updatedOffer, Set<Tag> tags, OfferPhoto photo) {
        this.title = updatedOffer.getTitle();
        this.description = updatedOffer.getDescription();
        this.dateAndTime = updatedOffer.getDateAndTime();
        this.salary = updatedOffer.getSalary();
        this.maxApplications = updatedOffer.getMaxApplications();
        this.tags = tags;
        this.photo = photo;
        this.contract = updatedOffer.getContract();
    }

    public void addApplication(Application application) {
        applications.add(application);
        application.setOffer(this);
    }
}
