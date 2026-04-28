package com.finance.backend.emi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmiPaymentRequest(
        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
        BigDecimal paymentAmount,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
