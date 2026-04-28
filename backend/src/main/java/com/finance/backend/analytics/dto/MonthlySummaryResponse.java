package com.finance.backend.analytics.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record MonthlySummaryResponse(
        Long userId,
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal savings,
        String topSpendingCategory,
        BigDecimal topSpendingAmount) {
}
