package com.feellog.backend.domain.report.util;

import java.util.Locale;

public enum ExpenseSortType {
    LATEST, OLDEST, AMOUNT_HIGH, AMOUNT_LOW;

    public static ExpenseSortType from(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return LATEST; // 기본값
        }
    }
}