package com.mimaja.job_finder_app.feature.offer.offerphoto.model;

import com.mimaja.job_finder_app.shared.model.Photo;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "offer_photos")
public class OfferPhoto extends Photo {}
