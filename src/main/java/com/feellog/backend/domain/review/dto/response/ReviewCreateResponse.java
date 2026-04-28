package com.feellog.backend.domain.review.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ReviewCreateResponse(
        Long reviewId,
        LocalDate reviewDate,
        String title,
        String summaryText,
        String feedbackTitle,
        String feedbackText,
        String guideTitle,
        List<String> guideItems
) {
}