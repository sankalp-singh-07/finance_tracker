package com.finance.backend.alert.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record AlertsResponse(
        int totalAlerts,
        List<BudgetAlertResponse> budgetWarnings,
        List<EmiAlertResponse> emiReminders) {
}
