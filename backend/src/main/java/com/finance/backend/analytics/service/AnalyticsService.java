package com.finance.backend.analytics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.analytics.dto.FinancialHealthScoreResponse;
import com.finance.backend.analytics.dto.MonthlySummaryResponse;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.transaction.model.FinanceTransaction;
import com.finance.backend.transaction.service.FinanceTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final FinanceTransactionService transactionService;

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(Long userId, String month) {
        YearMonth yearMonth = parseMonth(month);
        BigDecimal totalIncome = transactionService.getTotalByType(
                userId,
                TransactionType.INCOME,
                yearMonth.atDay(1),
                yearMonth.atEndOfMonth());
        BigDecimal totalExpense = transactionService.getTotalByType(
                userId,
                TransactionType.EXPENSE,
                yearMonth.atDay(1),
                yearMonth.atEndOfMonth());
        BigDecimal savings = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categorySpend = transactionService
                .getExpenseTransactions(userId, yearMonth.atDay(1), yearMonth.atEndOfMonth())
                .stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, FinanceTransaction::getAmount, BigDecimal::add)));

        Optional<Map.Entry<String, BigDecimal>> topCategory = categorySpend.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        return MonthlySummaryResponse.builder()
                .userId(userId)
                .month(month)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .savings(savings)
                .topSpendingCategory(topCategory.map(Map.Entry::getKey).orElse(null))
                .topSpendingAmount(topCategory.map(Map.Entry::getValue).orElse(BigDecimal.ZERO))
                .build();
    }

    @Transactional(readOnly = true)
    public FinancialHealthScoreResponse getFinancialHealthScore(Long userId, String month) {
        MonthlySummaryResponse summary = getMonthlySummary(userId, month);
        BigDecimal totalIncome = summary.totalIncome();
        BigDecimal totalExpense = summary.totalExpense();

        BigDecimal savingsRatio = totalIncome.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : summary.savings().divide(totalIncome, 4, RoundingMode.HALF_UP);
        BigDecimal expenseRatio = totalIncome.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ONE
                : totalExpense.divide(totalIncome, 4, RoundingMode.HALF_UP);

        int score = 50;
        score += savingsRatio.multiply(BigDecimal.valueOf(40)).intValue();
        score += BigDecimal.ONE.subtract(expenseRatio).multiply(BigDecimal.valueOf(20)).intValue();
        score = Math.max(0, Math.min(100, score));

        String rating = score >= 80 ? "EXCELLENT"
                : score >= 65 ? "GOOD"
                : score >= 45 ? "AVERAGE"
                : "NEEDS_ATTENTION";

        return FinancialHealthScoreResponse.builder()
                .userId(userId)
                .month(month)
                .score(score)
                .savingsRatio(savingsRatio)
                .rating(rating)
                .build();
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException("Month must be in YYYY-MM format");
        }
    }
}
