package com.finance.backend.analytics.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.finance.backend.analytics.dto.FinancialHealthScoreResponse;
import com.finance.backend.analytics.dto.MonthlySummaryResponse;
import com.finance.backend.analytics.service.AnalyticsService;
import com.finance.backend.auth.security.AuthenticatedUserPrincipal;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytics")
@Validated
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse getMonthlySummary(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return analyticsService.getMonthlySummary(currentUser.getId(), month);
    }

    @GetMapping("/health-score")
    public FinancialHealthScoreResponse getFinancialHealthScore(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return analyticsService.getFinancialHealthScore(currentUser.getId(), month);
    }
}
