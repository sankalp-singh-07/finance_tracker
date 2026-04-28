package com.finance.backend.analytics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.analytics.dto.AnalyticsSummaryResponse;
import com.finance.backend.analytics.dto.FinancialHealthScoreResponse;
import com.finance.backend.analytics.dto.MonthlySummaryResponse;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final FinanceTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary(Long userId, String month) {
        AnalyticsSnapshot snapshot = buildSnapshot(userId, resolveMonth(month));

        return AnalyticsSummaryResponse.builder()
                .totalIncome(snapshot.totalIncome())
                .totalExpense(snapshot.totalExpense())
                .savings(snapshot.savings())
                .topSpendingCategory(snapshot.topSpendingCategory())
                .financialHealthScore(snapshot.financialHealthScore())
                .build();
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(Long userId, String month) {
        YearMonth yearMonth = parseMonth(month);
        AnalyticsSnapshot snapshot = buildSnapshot(userId, yearMonth);

        return MonthlySummaryResponse.builder()
                .userId(userId)
                .month(month)
                .totalIncome(snapshot.totalIncome())
                .totalExpense(snapshot.totalExpense())
                .savings(snapshot.savings())
                .topSpendingCategory(snapshot.topSpendingCategory())
                .topSpendingAmount(snapshot.topSpendingAmount())
                .build();
    }

    @Transactional(readOnly = true)
    public FinancialHealthScoreResponse getFinancialHealthScore(Long userId, String month) {
        AnalyticsSnapshot snapshot = buildSnapshot(userId, parseMonth(month));

        return FinancialHealthScoreResponse.builder()
                .userId(userId)
                .month(month)
                .score(snapshot.financialHealthScore())
                .savingsRatio(snapshot.savingsRatio())
                .rating(resolveRating(snapshot.financialHealthScore()))
                .build();
    }

    private AnalyticsSnapshot buildSnapshot(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId,
                TransactionType.INCOME,
                startDate,
                endDate);
        BigDecimal totalExpense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId,
                TransactionType.EXPENSE,
                startDate,
                endDate);
        BigDecimal savings = totalIncome.subtract(totalExpense);

        List<Object[]> topCategories = transactionRepository.findTopSpendingCategories(
                userId,
                startDate,
                endDate,
                PageRequest.of(0, 1));

        String topSpendingCategory = null;
        BigDecimal topSpendingAmount = BigDecimal.ZERO;
        if (!topCategories.isEmpty()) {
            Object[] row = topCategories.get(0);
            topSpendingCategory = Objects.toString(row[0], null);
            topSpendingAmount = row[1] instanceof BigDecimal amount
                    ? amount
                    : BigDecimal.valueOf(((Number) row[1]).doubleValue());
        }

        BigDecimal savingsRatio = totalIncome.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.ZERO
                : savings.max(BigDecimal.ZERO).divide(totalIncome, 4, RoundingMode.HALF_UP);
        int financialHealthScore = savingsRatio.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.DOWN)
                .min(BigDecimal.valueOf(100))
                .intValue();

        return new AnalyticsSnapshot(
                totalIncome,
                totalExpense,
                savings,
                topSpendingCategory,
                topSpendingAmount,
                savingsRatio,
                financialHealthScore);
    }

    private String resolveRating(int financialHealthScore) {
        if (financialHealthScore >= 80) {
            return "EXCELLENT";
        }
        if (financialHealthScore >= 60) {
            return "GOOD";
        }
        if (financialHealthScore >= 40) {
            return "AVERAGE";
        }
        return "NEEDS_ATTENTION";
    }

    private YearMonth resolveMonth(String month) {
        return month == null || month.isBlank() ? YearMonth.now() : parseMonth(month);
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException("Month must be in YYYY-MM format");
        }
    }

    private record AnalyticsSnapshot(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal savings,
            String topSpendingCategory,
            BigDecimal topSpendingAmount,
            BigDecimal savingsRatio,
            int financialHealthScore) {
    }
}
