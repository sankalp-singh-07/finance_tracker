package com.finance.backend.recurring.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.service.CategoryService;
import com.finance.backend.common.enums.RecurringFrequency;
import com.finance.backend.recurring.dto.RecurringGenerationRequest;
import com.finance.backend.recurring.dto.RecurringTransactionRequest;
import com.finance.backend.recurring.dto.RecurringTransactionResponse;
import com.finance.backend.recurring.model.RecurringTransaction;
import com.finance.backend.recurring.repository.RecurringTransactionRepository;
import com.finance.backend.transaction.dto.TransactionRequest;
import com.finance.backend.transaction.dto.TransactionResponse;
import com.finance.backend.transaction.service.FinanceTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final CategoryService categoryService;
    private final FinanceTransactionService transactionService;

    @Transactional
    public RecurringTransactionResponse createRecurringTransaction(Long userId, RecurringTransactionRequest request) {
        Category category = categoryService.getAccessibleCategory(request.categoryId(), userId);

        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .amount(request.amount())
                .category(category)
                .frequency(request.frequency())
                .nextExecutionDate(request.nextExecutionDate())
                .note(request.note() == null ? null : request.note().trim())
                .user(User.builder().id(userId).build())
                .build();

        return mapToResponse(recurringTransactionRepository.save(recurringTransaction));
    }

    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> getRecurringTransactions(Long userId) {
        return recurringTransactionRepository.findByUser_IdOrderByNextExecutionDateAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public List<TransactionResponse> generateTransactions(Long userId, RecurringGenerationRequest request) {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository
                .findByUser_IdAndNextExecutionDateLessThanEqualOrderByNextExecutionDateAsc(userId, request.asOfDate());

        List<TransactionResponse> generatedTransactions = new ArrayList<>();
        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            while (!recurringTransaction.getNextExecutionDate().isAfter(request.asOfDate())) {
                TransactionRequest transactionRequest = new TransactionRequest(
                        recurringTransaction.getAmount(),
                        recurringTransaction.getCategory().getType(),
                        recurringTransaction.getCategory().getId(),
                        List.of(),
                        recurringTransaction.getNextExecutionDate(),
                        recurringTransaction.getNote());
                generatedTransactions.add(transactionService.createTransaction(userId, transactionRequest));
                recurringTransaction.setNextExecutionDate(getNextExecutionDate(
                        recurringTransaction.getNextExecutionDate(),
                        recurringTransaction.getFrequency()));
            }
        }

        recurringTransactionRepository.saveAll(recurringTransactions);
        return generatedTransactions;
    }

    private LocalDate getNextExecutionDate(LocalDate currentDate, RecurringFrequency frequency) {
        return switch (frequency) {
            case WEEKLY -> currentDate.plusWeeks(1);
            case MONTHLY -> currentDate.plusMonths(1);
        };
    }

    private RecurringTransactionResponse mapToResponse(RecurringTransaction recurringTransaction) {
        Category category = recurringTransaction.getCategory();
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(category.getUserId())
                .defaultCategory(category.isDefaultCategory())
                .build();

        return RecurringTransactionResponse.builder()
                .id(recurringTransaction.getId())
                .amount(recurringTransaction.getAmount())
                .category(categoryResponse)
                .frequency(recurringTransaction.getFrequency())
                .nextExecutionDate(recurringTransaction.getNextExecutionDate())
                .note(recurringTransaction.getNote())
                .userId(recurringTransaction.getUser().getId())
                .build();
    }
}
