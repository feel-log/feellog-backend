package com.feellog.backend.domain.report.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface CategoryExpenseSummary {
    Long getCategoryId();
    String getName();
    BigDecimal getTotal();
    LocalTime getFirstTime();
    LocalDateTime getLastCreatedAt();
}