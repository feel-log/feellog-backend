package com.feellog.backend.domain.asset.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class AssetListResponse {

    private String categoryName;
    private BigDecimal categoryTotalAmount;

    private List<AssetSummaryResponse> assets;

    private int page;
    private int size;
    private boolean hasNext;
}