package com.finance.backend.alert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.alert.dto.AlertsResponse;
import com.finance.backend.alert.dto.BudgetAlertResponse;
import com.finance.backend.alert.dto.EmiAlertResponse;
import com.finance.backend.budget.dto.BudgetResponse;
import com.finance.backend.budget.service.BudgetService;
import com.finance.backend.emi.dto.EmiResponse;
import com.finance.backend.emi.service.EmiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {

    private static final BigDecimal BUDGET_WARNING_THRESHOLD = BigDecimal.valueOf(80);
    private static final int DEFAULT_EMI_LOOKAHEAD_DAYS = 7;

    private final BudgetService budgetService;
    private final EmiService emiService;

    @Transactional(readOnly = true)
    public AlertsResponse getAlerts(Long userId, Integer daysAhead) {
        int emiLookaheadDays = daysAhead == null ? DEFAULT_EMI_LOOKAHEAD_DAYS : daysAhead;
        List<BudgetAlertResponse> budgetWarnings = getBudgetWarnings(userId);
        List<EmiAlertResponse> emiReminders = getUpcomingEmiAlerts(userId, emiLookaheadDays);

        return AlertsResponse.builder()
                .totalAlerts(budgetWarnings.size() + emiReminders.size())
                .budgetWarnings(budgetWarnings)
                .emiReminders(emiReminders)
                .build();
    }

    private List<BudgetAlertResponse> getBudgetWarnings(Long userId) {
        String currentMonth = YearMonth.now().toString();

        return budgetService.getBudgets(userId, currentMonth).stream()
                .map(this::toBudgetAlert)
                .filter(alert -> alert != null)
                .toList();
    }

    private BudgetAlertResponse toBudgetAlert(BudgetResponse budget) {
        if (budget.monthlyLimit().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal usagePercentage = budget.usedAmount()
                .divide(budget.monthlyLimit(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (usagePercentage.compareTo(BUDGET_WARNING_THRESHOLD) < 0) {
            return null;
        }

        String severity = usagePercentage.compareTo(BigDecimal.valueOf(100)) >= 0 ? "HIGH" : "MEDIUM";
        String message = usagePercentage.compareTo(BigDecimal.valueOf(100)) >= 0
                ? "Budget limit has been exceeded for " + budget.category().name()
                : "Budget usage has crossed 80% for " + budget.category().name();

        return BudgetAlertResponse.builder()
                .budgetId(budget.id())
                .categoryId(budget.category().id())
                .categoryName(budget.category().name())
                .month(budget.month())
                .monthlyLimit(budget.monthlyLimit())
                .usedAmount(budget.usedAmount())
                .usagePercentage(usagePercentage.setScale(2, RoundingMode.HALF_UP))
                .severity(severity)
                .message(message)
                .build();
    }

    private List<EmiAlertResponse> getUpcomingEmiAlerts(Long userId, int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = today.plusDays(daysAhead);

        return emiService.getEmis(userId).stream()
                .filter(emi -> emi.remainingAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(emi -> toEmiAlert(emi, today, lastDate))
                .filter(alert -> alert != null)
                .toList();
    }

    private EmiAlertResponse toEmiAlert(EmiResponse emi, LocalDate today, LocalDate lastDate) {
        LocalDate dueDate = resolveNextDueDate(emi.startDate(), today);
        if (dueDate.isBefore(today) || dueDate.isAfter(lastDate)) {
            return null;
        }

        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);
        String severity = daysUntilDue <= 2 ? "HIGH" : "MEDIUM";
        String message = daysUntilDue == 0
                ? "EMI payment is due today for " + emi.name()
                : "EMI payment is due in " + daysUntilDue + " day(s) for " + emi.name();

        return EmiAlertResponse.builder()
                .emiId(emi.id())
                .name(emi.name())
                .monthlyEmi(emi.monthlyEmi())
                .remainingAmount(emi.remainingAmount())
                .dueDate(dueDate)
                .daysUntilDue(daysUntilDue)
                .severity(severity)
                .message(message)
                .build();
    }

    private LocalDate resolveNextDueDate(LocalDate startDate, LocalDate today) {
        if (!startDate.isBefore(today)) {
            return startDate;
        }

        YearMonth currentMonth = YearMonth.from(today);
        int dueDay = Math.min(startDate.getDayOfMonth(), currentMonth.lengthOfMonth());
        LocalDate candidate = currentMonth.atDay(dueDay);

        if (candidate.isBefore(today)) {
            YearMonth nextMonth = currentMonth.plusMonths(1);
            dueDay = Math.min(startDate.getDayOfMonth(), nextMonth.lengthOfMonth());
            return nextMonth.atDay(dueDay);
        }

        return candidate;
    }
}
