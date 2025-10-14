package com.mimaja.job_finder_app.feature.users.ratings.model;

import com.mimaja.job_finder_app.feature.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double rate;

    @Column(columnDefinition = "TEXT", length = 300)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
