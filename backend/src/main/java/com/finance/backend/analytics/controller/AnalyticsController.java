package com.finance.backend.analytics.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.analytics.dto.FinancialHealthScoreResponse;
import com.finance.backend.analytics.dto.MonthlySummaryResponse;
import com.finance.backend.analytics.service.AnalyticsService;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytics")
@Validated
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse getMonthlySummary(
            @RequestParam @Positive Long userId,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return analyticsService.getMonthlySummary(userId, month);
    }

    @GetMapping("/health-score")
    public FinancialHealthScoreResponse getFinancialHealthScore(
            @RequestParam @Positive Long userId,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return analyticsService.getFinancialHealthScore(userId, month);
    }
}
