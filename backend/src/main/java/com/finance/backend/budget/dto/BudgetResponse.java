package com.finance.backend.budget.dto;

import java.math.BigDecimal;

import com.finance.backend.category.dto.CategoryResponse;

import lombok.Builder;

@Builder
public record BudgetResponse(
        Long id,
        Long userId,
        String month,
        BigDecimal monthlyLimit,
        BigDecimal usedAmount,
        BigDecimal remainingAmount,
        CategoryResponse category) {
}
