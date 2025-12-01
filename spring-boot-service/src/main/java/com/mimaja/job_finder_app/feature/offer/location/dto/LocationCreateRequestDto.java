package com.mimaja.job_finder_app.feature.offer.location.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record LocationCreateRequestDto(@NotNull List<Double> coordinates) {}
