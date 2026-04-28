package com.finance.backend.recurring.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.recurring.model.RecurringTransaction;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByUserIdOrderByNextExecutionDateAsc(Long userId);

    List<RecurringTransaction> findByUserIdAndNextExecutionDateLessThanEqualOrderByNextExecutionDateAsc(Long userId, LocalDate asOfDate);
}
