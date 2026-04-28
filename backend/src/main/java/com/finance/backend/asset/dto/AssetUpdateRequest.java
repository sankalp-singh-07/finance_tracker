package com.finance.backend.asset.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssetUpdateRequest(
        @NotNull(message = "Updated value is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Asset value cannot be negative")
        BigDecimal value,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
