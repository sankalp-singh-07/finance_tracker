package com.finance.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name can have at most 120 characters")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 180, message = "Email can have at most 180 characters")
        String email) {
}
