package com.finance.backend.recurring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.auth.security.AuthenticatedUserPrincipal;
import com.finance.backend.recurring.dto.RecurringGenerationRequest;
import com.finance.backend.recurring.dto.RecurringTransactionRequest;
import com.finance.backend.recurring.dto.RecurringTransactionResponse;
import com.finance.backend.recurring.service.RecurringTransactionService;
import com.finance.backend.transaction.dto.TransactionResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/recurring-transactions", "/api/v1/recurring-transactions"})
@Validated
@RequiredArgsConstructor
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecurringTransactionResponse createRecurringTransaction(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody RecurringTransactionRequest request) {
        return recurringTransactionService.createRecurringTransaction(currentUser.getId(), request);
    }

    @GetMapping
    public List<RecurringTransactionResponse> getRecurringTransactions(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser) {
        return recurringTransactionService.getRecurringTransactions(currentUser.getId());
    }

    @GetMapping("/{recurringTransactionId}")
    public RecurringTransactionResponse getRecurringTransaction(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long recurringTransactionId) {
        return recurringTransactionService.getRecurringTransaction(currentUser.getId(), recurringTransactionId);
    }

    @PutMapping("/{recurringTransactionId}")
    public RecurringTransactionResponse updateRecurringTransaction(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long recurringTransactionId,
            @Valid @RequestBody RecurringTransactionRequest request) {
        return recurringTransactionService.updateRecurringTransaction(
                currentUser.getId(),
                recurringTransactionId,
                request);
    }

    @PostMapping("/generate")
    public List<TransactionResponse> generateTransactions(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody RecurringGenerationRequest request) {
        return recurringTransactionService.generateTransactions(currentUser.getId(), request);
    }

    @DeleteMapping("/{recurringTransactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecurringTransaction(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @PathVariable @Positive Long recurringTransactionId) {
        recurringTransactionService.deleteRecurringTransaction(currentUser.getId(), recurringTransactionId);
    }
}
