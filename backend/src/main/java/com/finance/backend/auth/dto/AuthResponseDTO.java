package com.finance.backend.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponseDTO(
        String token) {
}
