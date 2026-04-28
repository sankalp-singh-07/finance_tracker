package com.finance.backend.tag.dto;

import lombok.Builder;

@Builder
public record TagResponse(
        Long id,
        String name,
        Long userId) {
}
