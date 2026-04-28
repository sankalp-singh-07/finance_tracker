package com.finance.backend.emi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record EmiResponse(
        Long id,
        String name,
        BigDecimal totalAmount,
        BigDecimal monthlyEmi,
        BigDecimal remainingAmount,
        BigDecimal interestRate,
        LocalDate startDate,
        Long userId) {
}
