package com.feellog.backend.domain.review.dto.response;

public record ReviewSelectedResponse(
        Long emotionId,
        Long situationTagId,
        Long satisfactionOptionId,
        Long nextActionOptionId
) {
}