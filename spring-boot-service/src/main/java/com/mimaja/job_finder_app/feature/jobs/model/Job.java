package com.mimaja.job_finder_app.feature.jobs.model;import com.mimaja.job_finder_app.feature.jobs.locations.model.Location;
import com.mimaja.job_finder_app.feature.jobs.tags.model.Tag;
import com.mimaja.job_finder_app.feature.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private LocalDateTime startTime;

  @ManyToOne() private Location location;

  private Double salary;

  @ManyToMany(fetch = FetchType.LAZY)
  private Set<Tag> tags = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;

  @ManyToOne()
  @JoinColumn(name = "contractor_id")
  private User contractor;

  private JobStatus status;

  @Column(updatable = false)
  @CreationTimestamp
  private LocalDate createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;
}
