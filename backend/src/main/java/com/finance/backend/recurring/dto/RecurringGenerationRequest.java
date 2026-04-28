package com.finance.backend.recurring.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record RecurringGenerationRequest(
        @NotNull(message = "As of date is required")
        LocalDate asOfDate) {
}
