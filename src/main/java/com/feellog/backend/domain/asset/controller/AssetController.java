package com.feellog.backend.domain.asset.controller;

import com.feellog.backend.domain.asset.dto.request.AssetCreateRequest;
import com.feellog.backend.domain.asset.dto.request.AssetUpdateRequest;
import com.feellog.backend.domain.asset.dto.response.*;
import com.feellog.backend.domain.asset.entity.AssetSortType;
import com.feellog.backend.domain.asset.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/categories")
    public ResponseEntity<AssetCategoryListResponse> getAssetCategories() {
        AssetCategoryListResponse response = assetService.getAssetCategories();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public AssetCreateResponse createAsset(
            @AuthenticationPrincipal Long userId,
            @RequestBody AssetCreateRequest request
    ) {
        return assetService.createAsset(userId, request);
    }

    @GetMapping("/summary")
    public AssetSummaryTotalResponse getAssetSummary(
            @AuthenticationPrincipal Long userId
    ) {
        return assetService.getAssetSummary(userId);
    }

    @GetMapping
    public AssetListResponse getAssets(
            @AuthenticationPrincipal Long userId,

            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "LATEST") AssetSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return assetService.getAssets(userId, categoryId, sort, page, size);
    }

    @GetMapping("/{assetId}")
    public AssetDetailResponse getAsset(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long assetId
    ) {
        return assetService.getAsset(userId, assetId);
    }

    @PatchMapping("/{assetId}")
    public AssetUpdateResponse updateAsset(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long assetId,
            @Valid @RequestBody AssetUpdateRequest request
    ) {
        return assetService.updateAsset(userId, assetId, request);
    }

    @DeleteMapping("/{assetId}")
    public AssetDeleteResponse deleteAsset(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long assetId
    ) {
        return assetService.deleteAsset(userId, assetId);
    }
}