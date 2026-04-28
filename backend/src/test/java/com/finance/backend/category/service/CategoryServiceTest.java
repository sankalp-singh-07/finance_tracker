package com.finance.backend.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.backend.budget.repository.BudgetRepository;
import com.finance.backend.category.dto.CategoryRequest;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.repository.CategoryRepository;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.recurring.repository.RecurringTransactionRepository;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FinanceTransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void updateCategoryShouldPersistTrimmedNameAndType() {
        Long userId = 1L;
        Long categoryId = 10L;

        Category category = Category.builder()
                .id(categoryId)
                .name("Old")
                .type(TransactionType.EXPENSE)
                .userId(userId)
                .defaultCategory(false)
                .build();
        CategoryRequest request = new CategoryRequest("  Salary  ", TransactionType.INCOME);

        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIdAndIdNot("Salary", TransactionType.INCOME, userId, categoryId))
                .thenReturn(false);
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndDefaultCategoryTrue("Salary", TransactionType.INCOME))
                .thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponse response = categoryService.updateCategory(userId, categoryId, request);

        assertEquals("Salary", response.name());
        assertEquals(TransactionType.INCOME, response.type());
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteCategoryShouldFailWhenCategoryIsInUse() {
        Long userId = 1L;
        Long categoryId = 10L;

        Category category = Category.builder()
                .id(categoryId)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .userId(userId)
                .defaultCategory(false)
                .build();

        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByUser_IdAndCategory_Id(userId, categoryId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(userId, categoryId));
        verify(categoryRepository, never()).delete(category);
    }
}
