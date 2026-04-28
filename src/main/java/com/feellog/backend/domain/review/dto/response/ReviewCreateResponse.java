package com.feellog.backend.domain.review.dto.response;

import java.time.LocalDate;

public record ReviewCreateResponse(
        Long reviewId,
        LocalDate reviewDate,
        String title,
        String feedbackText,
        String guideText
) {
}