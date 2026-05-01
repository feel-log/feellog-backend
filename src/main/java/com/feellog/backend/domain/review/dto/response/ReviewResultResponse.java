package com.feellog.backend.domain.review.dto.response;

import java.util.List;

public record ReviewResultResponse(
        String feedbackTitle,
        String feedbackText,
        String guideTitle,
        List<String> guideItems
) {
}