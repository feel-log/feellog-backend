package com.feellog.backend.domain.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AssetCategoryListResponse {

    private List<AssetCategoryResponse> assetCategories;

}