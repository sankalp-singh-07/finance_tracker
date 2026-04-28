package com.finance.backend.recurring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.backend.auth.model.User;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.service.CategoryService;
import com.finance.backend.common.enums.RecurringFrequency;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.recurring.dto.RecurringTransactionRequest;
import com.finance.backend.recurring.dto.RecurringTransactionResponse;
import com.finance.backend.recurring.model.RecurringTransaction;
import com.finance.backend.recurring.repository.RecurringTransactionRepository;
import com.finance.backend.transaction.service.FinanceTransactionService;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceTest {

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private FinanceTransactionService transactionService;

    @InjectMocks
    private RecurringTransactionService recurringTransactionService;

    @Test
    void updateRecurringTransactionShouldReplaceEditableFields() {
        Long userId = 1L;
        Long recurringId = 30L;

        Category category = Category.builder()
                .id(5L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .userId(userId)
                .defaultCategory(false)
                .build();
        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .id(recurringId)
                .amount(BigDecimal.valueOf(1000))
                .category(category)
                .frequency(RecurringFrequency.MONTHLY)
                .nextExecutionDate(LocalDate.of(2026, 5, 1))
                .note("old")
                .user(User.builder().id(userId).build())
                .build();
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                BigDecimal.valueOf(1250),
                category.getId(),
                RecurringFrequency.WEEKLY,
                LocalDate.of(2026, 5, 2),
                "  revised note  ");

        when(recurringTransactionRepository.findByIdAndUser_Id(recurringId, userId))
                .thenReturn(Optional.of(recurringTransaction));
        when(categoryService.getAccessibleCategory(category.getId(), userId)).thenReturn(category);
        when(recurringTransactionRepository.save(recurringTransaction)).thenReturn(recurringTransaction);

        RecurringTransactionResponse response = recurringTransactionService.updateRecurringTransaction(
                userId,
                recurringId,
                request);

        assertEquals(BigDecimal.valueOf(1250), response.amount());
        assertEquals(RecurringFrequency.WEEKLY, response.frequency());
        assertEquals(LocalDate.of(2026, 5, 2), response.nextExecutionDate());
        assertEquals("revised note", response.note());
        verify(recurringTransactionRepository).save(recurringTransaction);
    }
}
