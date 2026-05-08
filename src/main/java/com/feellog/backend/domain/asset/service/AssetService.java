package com.feellog.backend.domain.asset.service;

import com.feellog.backend.domain.asset.dto.request.AssetCreateRequest;
import com.feellog.backend.domain.asset.dto.request.AssetUpdateRequest;
import com.feellog.backend.domain.asset.dto.response.*;
import com.feellog.backend.domain.asset.entity.Asset;
import com.feellog.backend.domain.asset.entity.AssetCategory;
import com.feellog.backend.domain.asset.entity.AssetSortType;
import com.feellog.backend.domain.asset.repository.AssetCategoryRepository;
import com.feellog.backend.domain.asset.repository.AssetRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.repository.UserRepository;

import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetRepository assetRepository;

    private final UserRepository userRepository;

    // ASS-01 자산 카테고리 목록 조회
    public AssetCategoryListResponse getAssetCategories() {
        List<AssetCategory> categories =
                assetCategoryRepository.findAllByOrderByIdAsc();

        List<AssetCategoryResponse> assetCategories = categories.stream()
                .map(AssetCategoryResponse::from)
                .toList();

        return AssetCategoryListResponse.builder()
                .assetCategories(assetCategories)
                .build();
    }

    // ASS-02 자산 추가
    @Transactional
    public AssetCreateResponse createAsset(Long userId, AssetCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        AssetCategory assetCategory = assetCategoryRepository.findById(request.getAssetCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_CATEGORY_NOT_FOUND));

        Asset asset = Asset.create(
                user,
                assetCategory,
                request.getAmount(),
                request.getAssetDate(),
                request.getMemo()
        );

        Asset savedAsset = assetRepository.save(asset);

        return AssetCreateResponse.builder()
                .assetId(savedAsset.getId())
                .message("자산 기록이 등록되었습니다.")
                .build();
    }

    //ASS-03 자산 조회
    public AssetSummaryTotalResponse getAssetSummary(Long userId) {

        // 전체 자산 합계
        BigDecimal totalAmount = assetRepository.sumTotalAmountByUserId(userId);

        // 카테고리 목록
        List<AssetCategory> categories = assetCategoryRepository.findAllByOrderByIdAsc();

        // 카테고리별 합계
        List<AssetCategorySummaryResponse> categorySummaries = categories.stream()
                .map(category -> {
                    BigDecimal categoryAmount =
                            assetRepository.sumTotalAmountByUserIdAndCategoryId(userId, category.getId());

                    return AssetCategorySummaryResponse.builder()
                            .assetCategoryId(category.getId())
                            .categoryName(category.getName())
                            .totalAmount(categoryAmount)
                            .build();
                })
                .toList();

        return AssetSummaryTotalResponse.builder()
                .totalAssetAmount(totalAmount)
                .categories(categorySummaries)
                .build();
    }

    // ASS-04 자산 목록 조회
    public AssetListResponse getAssets(
            Long userId,
            Long categoryId,
            AssetSortType sortType,
            int page,
            int size
    ) {

        // 정렬 생성
        Sort sort = createSort(sortType);

        Pageable pageable = PageRequest.of(page, size, sort);

        // 목록 조회
        Page<Asset> assetPage;

        if (categoryId != null) {
            assetPage = assetRepository
                    .findByUserIdAndAssetCategoryIdAndIsDeletedFalse(userId, categoryId, pageable);
        } else {
            assetPage = assetRepository
                    .findByUserIdAndIsDeletedFalse(userId, pageable);
        }

        // DTO 변환
        List<AssetSummaryResponse> assets = assetPage.getContent()
                .stream()
                .map(AssetSummaryResponse::from)
                .toList();

        // 카테고리 정보
        String categoryName = null;
        BigDecimal categoryTotalAmount = null;

        if (categoryId != null) {
            AssetCategory category = assetCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_CATEGORY_NOT_FOUND));

            categoryName = category.getName();
            categoryTotalAmount =
                    assetRepository.sumTotalAmountByUserIdAndCategoryId(userId, categoryId);
        }

        return AssetListResponse.builder()
                .categoryName(categoryName)
                .categoryTotalAmount(categoryTotalAmount)
                .assets(assets)
                .page(page)
                .size(size)
                .hasNext(assetPage.hasNext())
                .build();
    }

    // =============================
    // 정렬 생성
    // =============================
    private Sort createSort(AssetSortType sortType) {

        if (sortType == null) {
            return Sort.by(
                    Sort.Order.desc("assetDate"),
                    Sort.Order.desc("createdAt")
            );
        }

        return switch (sortType) {
            case LATEST -> Sort.by(
                    Sort.Order.desc("assetDate"),
                    Sort.Order.desc("createdAt")
            );

            case OLDEST -> Sort.by(
                    Sort.Order.asc("assetDate"),
                    Sort.Order.asc("createdAt")
            );

            case AMOUNT_ASC -> Sort.by(Sort.Direction.ASC, "amount");
            case AMOUNT_DESC -> Sort.by(Sort.Direction.DESC, "amount");
        };
    }

    @Transactional(readOnly = true)
    public AssetDetailResponse getAsset(Long userId, Long assetId) {
        Asset asset = assetRepository.findByIdAndUserIdAndIsDeletedFalse(assetId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_CATEGORY_NOT_FOUND));

        return AssetDetailResponse.from(asset);
    }

    @Transactional
    public AssetUpdateResponse updateAsset(Long userId, Long assetId, AssetUpdateRequest request) {
        Asset asset = assetRepository.findByIdAndUserIdAndIsDeletedFalse(assetId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_NOT_FOUND));

        AssetCategory assetCategory = assetCategoryRepository.findById(request.getAssetCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSET_CATEGORY_NOT_FOUND));

        asset.update(
                assetCategory,
                request.getAmount(),
                request.getAssetDate(),
                request.getMemo()
        );

        return AssetUpdateResponse.builder()
                .assetId(asset.getId())
                .message("자산 기록이 수정되었습니다.")
                .build();
    }

    @Transactional
    public AssetDeleteResponse deleteAsset(Long userId, Long assetId) {
        Asset asset = assetRepository.findByIdAndUserIdAndIsDeletedFalse(assetId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자산 기록입니다."));

        asset.delete();

        return AssetDeleteResponse.builder()
                .message("자산 기록이 삭제되었습니다.")
                .build();
    }


}