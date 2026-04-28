package com.finance.backend.recurring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.common.enums.RecurringFrequency;

import lombok.Builder;

@Builder
public record RecurringTransactionResponse(
        Long id,
        BigDecimal amount,
        CategoryResponse category,
        RecurringFrequency frequency,
        LocalDate nextExecutionDate,
        String note,
        Long userId) {
}
