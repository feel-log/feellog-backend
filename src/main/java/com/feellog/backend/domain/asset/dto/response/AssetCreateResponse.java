package com.feellog.backend.domain.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssetCreateResponse {

    private Long assetId;
    private String message;
}