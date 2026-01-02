package com.mimaja.job_finder_app.feature.contract.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ContractDevelopRequestDto(@NotBlank UUID contractId) {}
