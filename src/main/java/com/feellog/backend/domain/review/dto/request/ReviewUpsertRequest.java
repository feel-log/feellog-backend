package com.feellog.backend.domain.review.dto.request;

public record ReviewUpsertRequest(
        Long emotionId,
        Long situationTagId,
        Long satisfactionOptionId,
        Long nextActionOptionId
) {
}