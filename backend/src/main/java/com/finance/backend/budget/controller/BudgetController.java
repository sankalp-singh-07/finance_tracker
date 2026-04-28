package com.finance.backend.budget.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.budget.dto.BudgetRequest;
import com.finance.backend.budget.dto.BudgetResponse;
import com.finance.backend.budget.service.BudgetService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/budgets")
@Validated
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse setBudget(@Valid @RequestBody BudgetRequest request) {
        return budgetService.setBudget(request);
    }

    @GetMapping
    public List<BudgetResponse> getBudgets(
            @RequestParam @Positive Long userId,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return budgetService.getBudgets(userId, month);
    }

    @GetMapping("/categories/{categoryId}")
    public BudgetResponse getBudget(
            @RequestParam @Positive Long userId,
            @PathVariable @Positive Long categoryId,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$") String month) {
        return budgetService.getBudget(userId, categoryId, month);
    }
}
