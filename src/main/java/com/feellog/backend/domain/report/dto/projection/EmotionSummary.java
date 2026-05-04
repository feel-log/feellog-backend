package com.feellog.backend.domain.report.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EmotionSummary {
    Long getEmotionId();
    String getName();
    long getEmotionCount();
    BigDecimal getLinkedAmount();
    LocalDateTime getLastUsedAt();
}