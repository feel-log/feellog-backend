package com.feellog.backend.domain.report.util;

import org.springframework.data.domain.Sort;

public class ReportSortUtils {

    private ReportSortUtils() {}

    public static Sort buildSortOption(ExpenseSortType sort) {
        return switch (sort) {
            case LATEST -> Sort.by(
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("createdAt")
            );
            case OLDEST -> Sort.by(
                    Sort.Order.asc("expenseDate"),
                    Sort.Order.asc("createdAt")
            );
            case AMOUNT_HIGH -> Sort.by(
                    Sort.Order.desc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("createdAt")
            );
            case AMOUNT_LOW -> Sort.by(
                    Sort.Order.asc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("createdAt")
            );
        };
    }
}
