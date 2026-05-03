package com.feellog.backend.domain.asset.dto.response;

import com.feellog.backend.domain.asset.entity.Asset;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class AssetSummaryResponse {

    private Long assetId;
    private BigDecimal amount;
    private LocalDate assetDate;
    private String memo;

    public static AssetSummaryResponse from(Asset asset) {
        return AssetSummaryResponse.builder()
                .assetId(asset.getId())
                .amount(asset.getAmount())
                .assetDate(asset.getAssetDate())
                .memo(asset.getMemo())
                .build();
    }
}