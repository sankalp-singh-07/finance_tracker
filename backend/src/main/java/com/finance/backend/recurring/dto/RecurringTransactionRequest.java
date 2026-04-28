package com.finance.backend.recurring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.finance.backend.common.enums.RecurringFrequency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecurringTransactionRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Category id is required")
        @Positive(message = "Category id must be positive")
        Long categoryId,
        @NotNull(message = "Frequency is required")
        RecurringFrequency frequency,
        @NotNull(message = "Next execution date is required")
        LocalDate nextExecutionDate,
        @Size(max = 500, message = "Note can have at most 500 characters")
        String note,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
