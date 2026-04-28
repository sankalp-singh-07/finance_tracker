package com.finance.backend.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "Tag name is required")
        @Size(max = 100, message = "Tag name can have at most 100 characters")
        String name,
        @NotNull(message = "User id is required")
        @Positive(message = "User id must be positive")
        Long userId) {
}
