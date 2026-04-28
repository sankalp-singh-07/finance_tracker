package com.finance.backend.reminder.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.backend.reminder.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUser_IdAndDueDateBetweenOrderByDueDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
}
