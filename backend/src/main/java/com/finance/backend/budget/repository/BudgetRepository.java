package com.finance.backend.budget.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.budget.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndCategory_IdAndMonth(Long userId, Long categoryId, String month);

    List<Budget> findByUserIdAndMonthOrderByIdAsc(Long userId, String month);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCategory_Id(Long userId, Long categoryId);
}
