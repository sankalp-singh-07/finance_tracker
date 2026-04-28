package com.finance.backend.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.budget.repository.BudgetRepository;
import com.finance.backend.category.dto.CategoryRequest;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.repository.CategoryRepository;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.recurring.repository.RecurringTransactionRepository;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FinanceTransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        String normalizedName = request.name().trim();
        boolean alreadyExists = categoryRepository.existsByNameIgnoreCaseAndTypeAndUserId(
                normalizedName,
                request.type(),
                userId);
        if (alreadyExists || categoryRepository.existsByNameIgnoreCaseAndTypeAndDefaultCategoryTrue(normalizedName, request.type())) {
            throw new BadRequestException("Category already exists for the requested type");
        }

        Category category = Category.builder()
                .name(normalizedName)
                .type(request.type())
                .userId(userId)
                .defaultCategory(false)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(Long userId) {
        return categoryRepository.findByDefaultCategoryTrueOrUserIdOrderByNameAsc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for user"));

        String normalizedName = request.name().trim();
        boolean duplicateForUser = categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIdAndIdNot(
                normalizedName,
                request.type(),
                userId,
                categoryId);
        boolean duplicateDefaultCategory = categoryRepository.existsByNameIgnoreCaseAndTypeAndDefaultCategoryTrue(
                normalizedName,
                request.type());
        if (duplicateForUser || duplicateDefaultCategory) {
            throw new BadRequestException("Category already exists for the requested type");
        }

        category.setName(normalizedName);
        category.setType(request.type());
        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for user"));

        boolean isUsedInTransactions = transactionRepository.existsByUser_IdAndCategory_Id(userId, categoryId);
        boolean isUsedInBudgets = budgetRepository.existsByUserIdAndCategory_Id(userId, categoryId);
        boolean isUsedInRecurringTransactions =
                recurringTransactionRepository.existsByUser_IdAndCategory_Id(userId, categoryId);

        if (isUsedInTransactions || isUsedInBudgets || isUsedInRecurringTransactions) {
            throw new BadRequestException("Category is in use and cannot be deleted");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Category getAccessibleCategory(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndDefaultCategoryTrueOrIdAndUserId(categoryId, categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for user"));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(category.getUserId())
                .defaultCategory(category.isDefaultCategory())
                .build();
    }
}
