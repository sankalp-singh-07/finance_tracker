package com.finance.backend.budget.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record BudgetRequest(
        @NotNull(message = "Category id is required")
        @Positive(message = "Category id must be positive")
        Long categoryId,
        @NotNull(message = "Monthly limit is required")
        @DecimalMin(value = "0.01", message = "Monthly limit must be greater than zero")
        BigDecimal monthlyLimit,
        @NotBlank(message = "Budget month is required")
        @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Month must be in YYYY-MM format")
        String month) {
}
