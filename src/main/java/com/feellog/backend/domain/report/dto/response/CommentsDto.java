package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

@Builder
public record CommentsDto(
        CommentDto categoryChange,
        CommentDto emotionTrend,
        CommentDto situationTrend,
        ConsecutiveTrendDto categoryConsecutive,
        ConsecutiveTrendDto emotionConsecutive,
        ConsecutiveTrendDto situationConsecutive
) {}
