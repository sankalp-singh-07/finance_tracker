package com.finance.backend.analytics.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record AnalyticsSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal savings,
        String topSpendingCategory,
        int financialHealthScore) {
}
