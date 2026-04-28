package com.finance.backend.user.dto;

import lombok.Builder;

@Builder
public record UserProfileResponseDTO(
        Long id,
        String name,
        String email) {
}
