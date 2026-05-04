package com.feellog.backend.domain.asset.repository;

import com.feellog.backend.domain.asset.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    // 전체 총 자산
    @Query("""
        SELECT COALESCE(SUM(a.amount), 0)
        FROM Asset a
        WHERE a.user.id = :userId
          AND a.isDeleted = false
    """)
    BigDecimal sumTotalAmountByUserId(@Param("userId") Long userId);

    // 특정 카테고리 총 자산
    @Query("""
        SELECT COALESCE(SUM(a.amount), 0)
        FROM Asset a
        WHERE a.user.id = :userId
          AND a.assetCategory.id = :categoryId
          AND a.isDeleted = false
    """)
    BigDecimal sumTotalAmountByUserIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId
    );

    // 전체 목록 조회
    Page<Asset> findByUserIdAndIsDeletedFalse(
            Long userId,
            Pageable pageable
    );

    // 카테고리별 목록 조회
    Page<Asset> findByUserIdAndAssetCategoryIdAndIsDeletedFalse(
            Long userId,
            Long assetCategoryId,
            Pageable pageable
    );

    Optional<Asset> findByIdAndUserIdAndIsDeletedFalse(Long assetId, Long userId);

}