package com.finance.backend.reminder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.reminder.dto.ReminderRequest;
import com.finance.backend.reminder.dto.ReminderResponse;
import com.finance.backend.reminder.service.ReminderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reminders")
@Validated
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse createReminder(@Valid @RequestBody ReminderRequest request) {
        return reminderService.createReminder(request);
    }

    @GetMapping("/upcoming")
    public List<ReminderResponse> getUpcomingReminders(
            @RequestParam @Positive Long userId,
            @RequestParam(required = false) @Positive Integer daysAhead) {
        return reminderService.getUpcomingReminders(userId, daysAhead);
    }
}
