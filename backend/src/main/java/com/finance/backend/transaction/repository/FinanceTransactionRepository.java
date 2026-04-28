package com.finance.backend.transaction.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.transaction.model.FinanceTransaction;

public interface FinanceTransactionRepository
        extends JpaRepository<FinanceTransaction, Long>, JpaSpecificationExecutor<FinanceTransaction> {

    @Query("""
            select coalesce(sum(t.amount), 0)
            from FinanceTransaction t
            where t.userId = :userId
              and t.type = :type
              and t.date between :startDate and :endDate
            """)
    BigDecimal sumByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from FinanceTransaction t
            where t.userId = :userId
              and t.category.id = :categoryId
              and t.type = com.finance.backend.common.enums.TransactionType.EXPENSE
              and t.date between :startDate and :endDate
            """)
    BigDecimal sumExpensesByUserIdAndCategoryIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<FinanceTransaction> findByUserIdAndTypeAndDateBetween(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);
}
