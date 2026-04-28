package com.finance.backend.category.dto;

import com.finance.backend.common.enums.TransactionType;

import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        TransactionType type,
        Long userId,
        boolean defaultCategory) {
}
