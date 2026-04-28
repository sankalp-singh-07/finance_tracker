package com.finance.backend.recurring.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecurringGenerationRequest(
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId,
        @NotNull(message = "As of date is required")
        LocalDate asOfDate) {
}
