package com.feellog.backend.domain.asset.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssetUpdateResponse {

    private Long assetId;
    private String message;
}