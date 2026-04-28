package com.finance.backend.asset.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.finance.backend.common.enums.AssetType;

import lombok.Builder;

@Builder
public record AssetResponse(
        Long id,
        String name,
        AssetType type,
        BigDecimal value,
        LocalDateTime lastUpdated,
        Long userId) {
}
