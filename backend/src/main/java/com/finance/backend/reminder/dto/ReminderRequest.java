package com.finance.backend.reminder.dto;

import java.time.LocalDate;

import com.finance.backend.common.enums.ReminderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReminderRequest(
        @NotBlank(message = "Reminder title is required")
        @Size(max = 150, message = "Reminder title can have at most 150 characters")
        String title,
        @NotNull(message = "Due date is required")
        LocalDate dueDate,
        @NotNull(message = "Reminder type is required")
        ReminderType type,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
