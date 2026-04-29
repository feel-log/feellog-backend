package com.feellog.backend.domain.review.dto;

import java.util.List;

public record ReviewOptionsResponse(
        List<EmotionOptionResponse> emotions,
        List<SituationTagOptionResponse> situationTags,
        List<ReviewChoiceOptionResponse> satisfactionOptions,
        List<ReviewChoiceOptionResponse> nextActionOptions
) {
}