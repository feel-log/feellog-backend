package com.feellog.backend.domain.review.dto;

public record EmotionOptionResponse(
        Long emotionId,
        String emotionGroupName,
        String name
) {
}