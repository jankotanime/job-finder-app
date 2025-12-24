package com.mimaja.job_finder_app.feature.user.profilephoto.repository;

import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, UUID> {}
