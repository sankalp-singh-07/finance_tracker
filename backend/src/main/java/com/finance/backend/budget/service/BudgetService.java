package com.finance.backend.budget.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.budget.dto.BudgetRequest;
import com.finance.backend.budget.dto.BudgetResponse;
import com.finance.backend.budget.model.Budget;
import com.finance.backend.budget.repository.BudgetRepository;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.service.CategoryService;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.transaction.service.FinanceTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final FinanceTransactionService transactionService;

    @Transactional
    public BudgetResponse setBudget(BudgetRequest request) {
        Category category = categoryService.getAccessibleCategory(request.categoryId(), request.userId());
        if (category.getType() != TransactionType.EXPENSE) {
            throw new BadRequestException("Budgets can only be set for expense categories");
        }

        parseMonth(request.month());

        Budget budget = budgetRepository.findByUserIdAndCategory_IdAndMonth(
                        request.userId(),
                        request.categoryId(),
                        request.month())
                .orElseGet(() -> Budget.builder()
                        .userId(request.userId())
                        .category(category)
                        .month(request.month())
                        .build());

        budget.setCategory(category);
        budget.setMonthlyLimit(request.monthlyLimit());
        budget.setMonth(request.month());

        return mapToResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(Long userId, String month) {
        parseMonth(month);
        return budgetRepository.findByUserIdAndMonthOrderByIdAsc(userId, month).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudget(Long userId, Long categoryId, String month) {
        parseMonth(month);
        Budget budget = budgetRepository.findByUserIdAndCategory_IdAndMonth(userId, categoryId, month)
                .orElseThrow(() -> new BadRequestException("Budget not found for user, category, and month"));
        return mapToResponse(budget);
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException("Month must be in YYYY-MM format");
        }
    }

    private BudgetResponse mapToResponse(Budget budget) {
        YearMonth yearMonth = parseMonth(budget.getMonth());
        BigDecimal usedAmount = transactionService.getCategoryExpenseTotal(
                budget.getUserId(),
                budget.getCategory().getId(),
                yearMonth.atDay(1),
                yearMonth.atEndOfMonth());
        BigDecimal remainingAmount = budget.getMonthlyLimit().subtract(usedAmount);

        Category category = budget.getCategory();
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(category.getUserId())
                .defaultCategory(category.isDefaultCategory())
                .build();

        return BudgetResponse.builder()
                .id(budget.getId())
                .userId(budget.getUserId())
                .month(budget.getMonth())
                .monthlyLimit(budget.getMonthlyLimit())
                .usedAmount(usedAmount)
                .remainingAmount(remainingAmount)
                .category(categoryResponse)
                .build();
    }
}
