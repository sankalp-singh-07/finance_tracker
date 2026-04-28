package com.finance.backend.analytics.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record FinancialHealthScoreResponse(
        Long userId,
        String month,
        int score,
        BigDecimal savingsRatio,
        String rating) {
}
