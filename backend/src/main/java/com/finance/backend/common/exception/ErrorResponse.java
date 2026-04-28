package com.finance.backend.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors) {
}
