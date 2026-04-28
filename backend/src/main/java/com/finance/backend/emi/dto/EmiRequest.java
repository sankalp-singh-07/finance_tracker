package com.finance.backend.emi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmiRequest(
        @NotBlank(message = "Loan name is required")
        @Size(max = 150, message = "Loan name can have at most 150 characters")
        String name,
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
        BigDecimal totalAmount,
        @NotNull(message = "Monthly EMI is required")
        @DecimalMin(value = "0.01", message = "Monthly EMI must be greater than zero")
        BigDecimal monthlyEmi,
        @NotNull(message = "Remaining amount is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Remaining amount cannot be negative")
        BigDecimal remainingAmount,
        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Interest rate cannot be negative")
        BigDecimal interestRate,
        @NotNull(message = "Start date is required")
        LocalDate startDate) {
}
