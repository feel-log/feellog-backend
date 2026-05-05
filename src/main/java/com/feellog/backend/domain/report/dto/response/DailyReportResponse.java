package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record DailyReportResponse(
        Period period,
        Summary summary,
        ExpenseGraph expenseGraph,
        Emotions emotions
) {
    @Builder
    public record Period(
            String date,
            String dayOfWeek
    ) {}

    @Builder
    public record Summary(
            long totalExpenseAmount
    ) {}

    @Builder
    public record ExpenseGraph(
            String displayType,
            String mainMessage,
            String subMessage,
            int topRatio,
            List<TopCategory> topCategories,
            SecondCategory secondCategory,
            int extraCount
    ) {
        @Builder
        public record TopCategory(
                String label,
                long amount
        ) {}

        @Builder
        public record SecondCategory(
                String label,
                long amount
        ) {}
    }

    @Builder
    public record Emotions(
            List<EmotionItem> list,
            boolean isEmpty
    ) {
        @Builder
        public record EmotionItem(
                Long emotionId,
                String emotionName,
                long emotionCount,
                int rank
        ) {}
    }
}