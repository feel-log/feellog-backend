package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record WeeklyReportResponse(
        LocalDate weekStart,
        LocalDate weekEnd,
        Long totalExpense,
        List<DailyAmountDto> dailyAmounts
) {
    @Builder
    public record DailyAmountDto(
            LocalDate date,
            Long expense
    ) {}
}