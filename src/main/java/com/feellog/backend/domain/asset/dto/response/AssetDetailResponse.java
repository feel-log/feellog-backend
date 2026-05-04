package com.feellog.backend.domain.asset.dto.response;

import com.feellog.backend.domain.asset.entity.Asset;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class AssetDetailResponse {

    private Long assetId;
    private Long assetCategoryId;
    private String assetCategoryName;
    private BigDecimal amount;
    private LocalDate assetDate;
    private String memo;

    public static AssetDetailResponse from(Asset asset) {
        return AssetDetailResponse.builder()
                .assetId(asset.getId())
                .assetCategoryId(asset.getAssetCategory().getId())
                .assetCategoryName(asset.getAssetCategory().getName())
                .amount(asset.getAmount())
                .assetDate(asset.getAssetDate())
                .memo(asset.getMemo())
                .build();
    }
}