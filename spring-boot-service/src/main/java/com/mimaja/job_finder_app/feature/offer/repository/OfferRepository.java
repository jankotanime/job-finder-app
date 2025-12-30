package com.mimaja.job_finder_app.feature.offer.repository;

import com.mimaja.job_finder_app.feature.offer.model.Offer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferRepository
        extends JpaRepository<Offer, UUID>, JpaSpecificationExecutor<Offer> {
    List<Offer> findAllOffersByOwnerId(UUID userId);

    List<Offer> findAllOffersByChosenCandidateId(UUID chosenCandidateId);

    @Query("SELECT o FROM Offer o JOIN o.applications a WHERE a.candidate = :candidateId")
    List<Offer> findAllOffersContainsUserInApplications(@Param("candidateId") UUID candidateId);

    @Query("SElECT o FROM Offer o WHERE o.owner.id = :userId")
    List<Offer> findOffersByOwnerId(UUID userId);
}
