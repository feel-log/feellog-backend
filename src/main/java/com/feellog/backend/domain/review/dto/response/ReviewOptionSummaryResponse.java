package com.feellog.backend.domain.review.dto.response;

public record ReviewOptionSummaryResponse(
        String situationTagName,
        Integer satisfactionScore,
        String nextActionOptionText
) {
}