package com.feellog.backend.domain.review.dto.response;

public record ReviewTitleResponse(
        String prefixText,   // 나를 위한 보상보다
        String highlightText, // 아쉬움
        String suffixText    // 이 더 컸던 오늘
) {
}