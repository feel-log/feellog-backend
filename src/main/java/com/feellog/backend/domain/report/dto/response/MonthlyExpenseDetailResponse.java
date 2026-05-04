package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MonthlyExpenseDetailResponse(
        PeriodDto period,
        Long totalAmount,
        int totalElements,
        int totalPages,
        int currentPage,
        List<DailyLogDto> dailyLogs,
        List<ExpenseDto> expenses
) {
    @Builder
    public record PeriodDto(
            LocalDate startDate,
            LocalDate endDate
    ) {}

    @Builder
    public record DailyLogDto(
            LocalDate date,
            List<ExpenseDto> expenses
    ) {}

    @Builder
    public record ExpenseDto(
            Long expenseId,
            LocalDate date,
            String categoryName,
            String memo,
            Long amount,
            String paymentMethod,
            List<EmotionDto> emotions,
            List<SituationTagDto> situationTags
    ) {}

    @Builder
    public record EmotionDto(
            Long emotionId,
            String emotionName
    ) {}

    @Builder
    public record SituationTagDto(
            Long situationTagId,
            String situationName
    ) {}
}