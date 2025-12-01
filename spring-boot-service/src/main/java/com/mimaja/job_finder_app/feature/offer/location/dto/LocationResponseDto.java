package com.mimaja.job_finder_app.feature.offer.location.dto;

import java.util.List;
import java.util.UUID;

public record LocationResponseDto(UUID id, List<Double> coordinates) {}
