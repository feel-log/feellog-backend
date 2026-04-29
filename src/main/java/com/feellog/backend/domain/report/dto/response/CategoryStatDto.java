package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

@Builder
public record CategoryStatDto(
        Long categoryId,
        String categoryName,
        String categoryGroupName,
        Long totalAmount,
        Double shareRate,
        Integer shareRateDisplay,
        Integer rank
) {}
