package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ConsecutiveTrendDto(
        int months,
        List<String> names,
        String message        // null이면 미노출
) {}