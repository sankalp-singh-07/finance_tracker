package com.finance.backend.recurring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.recurring.dto.RecurringGenerationRequest;
import com.finance.backend.recurring.dto.RecurringTransactionRequest;
import com.finance.backend.recurring.dto.RecurringTransactionResponse;
import com.finance.backend.recurring.service.RecurringTransactionService;
import com.finance.backend.transaction.dto.TransactionResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/recurring-transactions")
@Validated
@RequiredArgsConstructor
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecurringTransactionResponse createRecurringTransaction(
            @Valid @RequestBody RecurringTransactionRequest request) {
        return recurringTransactionService.createRecurringTransaction(request);
    }

    @GetMapping
    public List<RecurringTransactionResponse> getRecurringTransactions(@RequestParam @Positive Long userId) {
        return recurringTransactionService.getRecurringTransactions(userId);
    }

    @PostMapping("/generate")
    public List<TransactionResponse> generateTransactions(@Valid @RequestBody RecurringGenerationRequest request) {
        return recurringTransactionService.generateTransactions(request);
    }
}
