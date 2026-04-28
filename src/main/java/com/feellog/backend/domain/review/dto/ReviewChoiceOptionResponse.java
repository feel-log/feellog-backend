package com.feellog.backend.domain.review.dto;

public record ReviewChoiceOptionResponse(
        Long optionId,
        String optionText,
        String optionValue,
        Integer score
) {
}