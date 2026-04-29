package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

@Builder
public record EmotionStatDto(
        Long emotionId,
        String emotionName,
        String emotionGroupName,
        Long linkedAmount,
        Integer emotionCount,
        Integer rank
) {}
