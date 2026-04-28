package com.finance.backend.alert.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record EmiAlertResponse(
        Long emiId,
        String name,
        BigDecimal monthlyEmi,
        BigDecimal remainingAmount,
        LocalDate dueDate,
        long daysUntilDue,
        String severity,
        String message) {
}
