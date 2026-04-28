package com.finance.backend.alert.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record BudgetAlertResponse(
        Long budgetId,
        Long categoryId,
        String categoryName,
        String month,
        BigDecimal monthlyLimit,
        BigDecimal usedAmount,
        BigDecimal usagePercentage,
        String severity,
        String message) {
}
