package com.finance.backend.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.finance.backend.common.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransactionRequest(
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Transaction type is required")
        TransactionType type,
        @NotNull(message = "Category id is required")
        @Positive(message = "Category id must be positive")
        Long categoryId,
        List<@Positive(message = "Tag id must be positive") Long> tagIds,
        @NotNull(message = "Transaction date is required")
        LocalDate date,
        @Size(max = 500, message = "Note can have at most 500 characters")
        String note) {
}
