package com.finance.backend.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.category.model.Category;
import com.finance.backend.category.service.CategoryService;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.tag.dto.TagResponse;
import com.finance.backend.tag.model.Tag;
import com.finance.backend.tag.service.TagService;
import com.finance.backend.transaction.dto.TransactionRequest;
import com.finance.backend.transaction.dto.TransactionResponse;
import com.finance.backend.transaction.model.FinanceTransaction;
import com.finance.backend.transaction.repository.FinanceTransactionRepository;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceTransactionService {

    private final FinanceTransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        Category category = categoryService.getAccessibleCategory(request.categoryId(), userId);
        validateTransactionType(category, request.type());

        List<Tag> tags = tagService.getTagsForUser(userId, request.tagIds());

        FinanceTransaction transaction = FinanceTransaction.builder()
                .user(User.builder().id(userId).build())
                .amount(request.amount())
                .type(request.type())
                .category(category)
                .tags(new LinkedHashSet<>(tags))
                .date(request.date())
                .note(request.note() == null ? null : request.note().trim())
                .build();

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            TransactionType type) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        if (categoryId != null) {
            categoryService.getAccessibleCategory(categoryId, userId);
        }

        Specification<FinanceTransaction> specification = Specification.where(hasUserId(userId))
                .and(hasStartDate(startDate))
                .and(hasEndDate(endDate))
                .and(hasCategoryId(categoryId))
                .and(hasType(type));

        return transactionRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "date", "id")).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long userId, Long transactionId) {
        FinanceTransaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for user"));
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        FinanceTransaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for user"));
        Category category = categoryService.getAccessibleCategory(request.categoryId(), userId);
        validateTransactionType(category, request.type());
        List<Tag> tags = tagService.getTagsForUser(userId, request.tagIds());

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCategory(category);
        transaction.setTags(new LinkedHashSet<>(tags));
        transaction.setDate(request.date());
        transaction.setNote(request.note() == null ? null : request.note().trim());
        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        FinanceTransaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for user"));
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByType(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumByUserIdAndTypeAndDateBetween(userId, type, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCategoryExpenseTotal(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumExpensesByUserIdAndCategoryIdAndDateBetween(userId, categoryId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<FinanceTransaction> getExpenseTransactions(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUser_IdAndTypeAndDateBetween(userId, TransactionType.EXPENSE, startDate, endDate);
    }

    private void validateTransactionType(Category category, TransactionType transactionType) {
        if (category.getType() != transactionType) {
            throw new BadRequestException("Transaction type must match the selected category type");
        }
    }

    private Specification<FinanceTransaction> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            root.fetch("category", JoinType.LEFT);
            root.fetch("tags", JoinType.LEFT);
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

    private Specification<FinanceTransaction> hasStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> startDate == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
    }

    private Specification<FinanceTransaction> hasEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> endDate == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
    }

    private Specification<FinanceTransaction> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> categoryId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<FinanceTransaction> hasType(TransactionType type) {
        return (root, query, criteriaBuilder) -> type == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("type"), type);
    }

    private TransactionResponse mapToResponse(FinanceTransaction transaction) {
        Category category = transaction.getCategory();
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(category.getUserId())
                .defaultCategory(category.isDefaultCategory())
                .build();

        List<TagResponse> tagResponses = transaction.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .userId(tag.getUser().getId())
                        .build())
                .toList();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(categoryResponse)
                .tags(tagResponses)
                .date(transaction.getDate())
                .note(transaction.getNote())
                .build();
    }
}
