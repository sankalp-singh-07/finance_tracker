package com.finance.backend.recurring.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.recurring.model.RecurringTransaction;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByUser_IdOrderByNextExecutionDateAsc(Long userId);

    List<RecurringTransaction> findByUser_IdAndNextExecutionDateLessThanEqualOrderByNextExecutionDateAsc(Long userId, LocalDate asOfDate);

    Optional<RecurringTransaction> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndCategory_Id(Long userId, Long categoryId);
}
