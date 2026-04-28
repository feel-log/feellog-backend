package com.feellog.backend.domain.review.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ReviewDetailResponse(
        Long reviewId,
        LocalDate reviewDate,

        Long emotionId,
        String emotionName,

        Long situationTagId,
        String situationTagName,

        Long satisfactionOptionId,
        String satisfactionText,

        Long nextActionOptionId,
        String nextActionText,

        String title,
        String summaryText,

        String feedbackTitle,
        String feedbackText,

        String guideTitle,
        List<String> guideItems
) {
}