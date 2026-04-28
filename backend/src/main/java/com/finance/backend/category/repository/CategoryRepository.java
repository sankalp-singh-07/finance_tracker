package com.finance.backend.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.category.model.Category;
import com.finance.backend.common.enums.TransactionType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByDefaultCategoryTrueOrUserIdOrderByNameAsc(Long userId);

    Optional<Category> findByIdAndDefaultCategoryTrueOrIdAndUserId(Long defaultCategoryId, Long categoryId, Long userId);

    boolean existsByNameIgnoreCaseAndTypeAndUserId(String name, TransactionType type, Long userId);

    boolean existsByNameIgnoreCaseAndTypeAndDefaultCategoryTrue(String name, TransactionType type);

    long countByDefaultCategoryTrue();
}
