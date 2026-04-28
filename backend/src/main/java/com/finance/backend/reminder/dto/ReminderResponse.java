package com.finance.backend.reminder.dto;

import java.time.LocalDate;

import com.finance.backend.common.enums.ReminderType;

import lombok.Builder;

@Builder
public record ReminderResponse(
        Long id,
        String title,
        LocalDate dueDate,
        ReminderType type,
        Long userId) {
}
