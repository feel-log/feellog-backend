package com.feellog.backend.domain.asset.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AssetCategorySummaryResponse {

    private Long assetCategoryId;
    private String categoryName;
    private BigDecimal totalAmount;
}