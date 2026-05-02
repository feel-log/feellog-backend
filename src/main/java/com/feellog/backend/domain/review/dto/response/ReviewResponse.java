package com.feellog.backend.domain.review.dto.response;

import java.time.LocalDate;

public record ReviewResponse(
        Long reviewId,
        LocalDate reviewDate,
        ReviewTitleResponse title,
        ReviewOptionSummaryResponse options,
        ReviewResultResponse result
) {
}