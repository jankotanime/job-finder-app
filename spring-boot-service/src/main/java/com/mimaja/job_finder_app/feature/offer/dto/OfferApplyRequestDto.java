package com.mimaja.job_finder_app.feature.offer.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OfferApplyRequestDto(@NotNull UUID cvId) {}
