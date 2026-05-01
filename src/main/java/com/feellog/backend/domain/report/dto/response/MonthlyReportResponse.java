package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MonthlyReportResponse(
        PeriodDto period,
        SummaryDto summary,
        CommentsDto comments,
        CategoriesDto categories,
        EmotionsDto emotions,
        SituationsDto situations
) {
    @Builder
    public record PeriodDto(
            LocalDate startDate,
            LocalDate endDate
    ) {}

    @Builder
    public record SummaryDto(
            Long totalIncome,
            Long totalExpense
    ) {}

    @Builder
    public record CategoriesDto(List<CategoryStatDto> list) {}

    @Builder
    public record EmotionsDto(List<EmotionStatDto> list) {}

    @Builder
    public record SituationsDto(List<SituationStatDto> list) {}
}
