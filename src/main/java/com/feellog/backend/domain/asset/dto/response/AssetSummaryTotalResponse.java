package com.feellog.backend.domain.asset.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class AssetSummaryTotalResponse {

    private BigDecimal totalAssetAmount;
    private List<AssetCategorySummaryResponse> categories;
}