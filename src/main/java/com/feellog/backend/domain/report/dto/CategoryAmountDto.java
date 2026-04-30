package com.feellog.backend.domain.report.dto;

import java.math.BigDecimal;

public record CategoryAmountDto(Long categoryId, BigDecimal amount) {}