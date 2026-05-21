package com.feellog.backend.domain.report.util;

public enum ExpenseSortType {
    LATEST, OLDEST, AMOUNT_HIGH, AMOUNT_LOW;

    public static ExpenseSortType from(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LATEST; // 기본값
        }
    }
}