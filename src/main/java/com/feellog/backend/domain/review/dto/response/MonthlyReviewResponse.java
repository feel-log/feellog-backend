package com.feellog.backend.domain.review.dto.response;

import java.util.List;

public record MonthlyReviewResponse(
        int year,
        int month,
        List<MonthlyReviewDayResponse> days
) {
}
