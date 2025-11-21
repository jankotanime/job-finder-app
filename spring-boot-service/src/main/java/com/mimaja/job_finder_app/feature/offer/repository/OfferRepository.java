package com.mimaja.job_finder_app.feature.offer.repository;

import com.mimaja.job_finder_app.feature.offer.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {}
