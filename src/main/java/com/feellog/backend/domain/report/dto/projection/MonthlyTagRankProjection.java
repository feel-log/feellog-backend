package com.feellog.backend.domain.report.dto.projection;

import java.math.BigDecimal;

public interface MonthlyTagRankProjection {
    Long getTagId();
    String getTagName();
    int getYear();
    int getMonth();
    BigDecimal getScore(); // 카테고리: SUM(amount), 감정/상황: COUNT
}