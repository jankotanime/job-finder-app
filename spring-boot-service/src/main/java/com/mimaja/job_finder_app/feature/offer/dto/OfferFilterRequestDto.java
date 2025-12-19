package com.mimaja.job_finder_app.feature.offer.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OfferFilterRequestDto(
        LocalDateTime firstDate,
        LocalDateTime lastDate,
        Double minSalary,
        Double maxSalary,
        Set<UUID> categories,
        Set<UUID> tags) {}
