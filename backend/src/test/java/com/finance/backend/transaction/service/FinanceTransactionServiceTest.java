package com.finance.backend.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.backend.auth.model.User;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.service.CategoryService;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.tag.model.Tag;
import com.finance.backend.tag.service.TagService;
import com.finance.backend.transaction.dto.TransactionRequest;
import com.finance.backend.transaction.dto.TransactionResponse;
import com.finance.backend.transaction.model.FinanceTransaction;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

@ExtendWith(MockitoExtension.class)
class FinanceTransactionServiceTest {

    @Mock
    private FinanceTransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private FinanceTransactionService financeTransactionService;

    @Test
    void updateTransactionShouldReplaceAllEditableFields() {
        Long userId = 1L;
        Long transactionId = 100L;

        Category category = Category.builder()
                .id(3L)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .userId(userId)
                .defaultCategory(false)
                .build();
        Tag tag = Tag.builder()
                .id(7L)
                .name("Monthly")
                .user(User.builder().id(userId).build())
                .build();
        FinanceTransaction transaction = FinanceTransaction.builder()
                .id(transactionId)
                .user(User.builder().id(userId).build())
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.EXPENSE)
                .category(category)
                .tags(Set.of())
                .date(LocalDate.of(2026, 4, 10))
                .note("old")
                .build();

        TransactionRequest request = new TransactionRequest(
                BigDecimal.valueOf(220.50),
                TransactionType.EXPENSE,
                category.getId(),
                List.of(tag.getId()),
                LocalDate.of(2026, 4, 11),
                "  updated note  ");

        when(transactionRepository.findByIdAndUser_Id(transactionId, userId)).thenReturn(Optional.of(transaction));
        when(categoryService.getAccessibleCategory(category.getId(), userId)).thenReturn(category);
        when(tagService.getTagsForUser(userId, List.of(tag.getId()))).thenReturn(List.of(tag));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponse response =
                financeTransactionService.updateTransaction(userId, transactionId, request);

        assertEquals(BigDecimal.valueOf(220.50), response.amount());
        assertEquals(LocalDate.of(2026, 4, 11), response.date());
        assertEquals("updated note", response.note());
        assertEquals(1, response.tags().size());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void getTransactionShouldThrowWhenNotFound() {
        when(transactionRepository.findByIdAndUser_Id(999L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> financeTransactionService.getTransaction(1L, 999L));
    }
}
