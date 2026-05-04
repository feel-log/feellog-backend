package com.feellog.backend.domain.asset.dto.response;

import com.feellog.backend.domain.asset.entity.AssetCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssetCategoryResponse {

    private Long assetCategoryId;
    private String name;

    public static AssetCategoryResponse from(AssetCategory assetCategory) {
        return AssetCategoryResponse.builder()
                .assetCategoryId(assetCategory.getId())
                .name(assetCategory.getName())
                .build();
    }
}