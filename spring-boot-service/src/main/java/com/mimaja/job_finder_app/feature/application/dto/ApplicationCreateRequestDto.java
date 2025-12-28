package com.mimaja.job_finder_app.feature.application.dto;

import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.user.model.User;
import jakarta.validation.constraints.NotNull;

public record ApplicationCreateRequestDto(
        @NotNull User candidate, @NotNull Offer offer, @NotNull Cv chosenCv) {}
