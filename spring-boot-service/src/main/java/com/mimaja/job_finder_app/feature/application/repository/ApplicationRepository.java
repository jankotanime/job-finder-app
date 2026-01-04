package com.mimaja.job_finder_app.feature.application.repository;

import com.mimaja.job_finder_app.feature.application.model.Application;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    @Query("DELETE FROM Application a WHERE a.chosenCv.id = :cvId")
    @Modifying
    void deleteAllByChosenCvId(UUID cvId);

    @Query("DELETE FROM Application a WHERE a.candidate.id = :candidateId")
    @Modifying
    void deleteAllByCandidateId(UUID candidateId);

    @Query("SELECT a FROM Application a WHERE a.offer.id = :offerId")
    Page<Application> findAllByOfferId(UUID offerId, Pageable pageable);
}
