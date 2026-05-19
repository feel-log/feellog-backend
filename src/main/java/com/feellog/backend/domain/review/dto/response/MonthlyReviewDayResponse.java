package com.feellog.backend.domain.review.dto.response;

import java.time.LocalDate;

public record MonthlyReviewDayResponse(
        LocalDate date,
        boolean written
) {
}
