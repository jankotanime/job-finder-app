package com.mimaja.job_finder_app.feature.user.model;

import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    @Nullable private String passwordHash;

    private int phoneNumber;

    @Nullable private String googleId;

    @Column(columnDefinition = "TEXT", length = 500)
    private String profileDescription;

    @OneToMany private Set<Cv> cvs = new HashSet<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ProfilePhoto profilePhoto;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

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
