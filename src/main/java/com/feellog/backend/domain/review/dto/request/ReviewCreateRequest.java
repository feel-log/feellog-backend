package com.feellog.backend.domain.review.dto.request;

import java.time.LocalDate;

public record ReviewCreateRequest(
        LocalDate reviewDate,
        Long emotionId,
        Long situationTagId,
        Long satisfactionOptionId,
        Long nextActionOptionId
) {
}