package com.finance.backend.reminder.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
import com.finance.backend.reminder.dto.ReminderRequest;
import com.finance.backend.reminder.dto.ReminderResponse;
import com.finance.backend.reminder.model.Reminder;
import com.finance.backend.reminder.repository.ReminderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    @Transactional
    public ReminderResponse createReminder(Long userId, ReminderRequest request) {
        Reminder reminder = Reminder.builder()
                .title(request.title().trim())
                .dueDate(request.dueDate())
                .type(request.type())
                .user(User.builder().id(userId).build())
                .build();
        return mapToResponse(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public List<ReminderResponse> getUpcomingReminders(Long userId, Integer daysAhead) {
        int window = daysAhead == null ? 30 : daysAhead;
        return reminderRepository.findByUser_IdAndDueDateBetweenOrderByDueDateAsc(
                        userId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(window))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReminderResponse mapToResponse(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .title(reminder.getTitle())
                .dueDate(reminder.getDueDate())
                .type(reminder.getType())
                .userId(reminder.getUser().getId())
                .build();
    }
}
