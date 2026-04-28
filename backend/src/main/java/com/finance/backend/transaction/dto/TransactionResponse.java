package com.finance.backend.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.finance.backend.category.dto.CategoryResponse;
import com.finance.backend.common.enums.TransactionType;
import com.finance.backend.tag.dto.TagResponse;

import lombok.Builder;

@Builder
public record TransactionResponse(
        Long id,
        Long userId,
        BigDecimal amount,
        TransactionType type,
        CategoryResponse category,
        List<TagResponse> tags,
        LocalDate date,
        String note) {
}
