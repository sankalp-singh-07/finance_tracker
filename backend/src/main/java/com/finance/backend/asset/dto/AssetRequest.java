package com.finance.backend.asset.dto;

import java.math.BigDecimal;

import com.finance.backend.common.enums.AssetType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AssetRequest(
        @NotBlank(message = "Asset name is required")
        @Size(max = 100, message = "Asset name can have at most 100 characters")
        String name,
        @NotNull(message = "Asset type is required")
        AssetType type,
        @NotNull(message = "Asset value is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Asset value cannot be negative")
        BigDecimal value,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
