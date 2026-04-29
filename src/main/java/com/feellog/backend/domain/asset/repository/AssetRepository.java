package com.feellog.backend.domain.asset.repository;

import com.feellog.backend.domain.asset.entity.Asset;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    // 사용자 기준 전체 자산 기록 조회
    List<Asset> findByUser(User user);

    // 특정 날짜 자산 기록 조회
    List<Asset> findByUserAndAssetDate(User user, LocalDate assetDate);

    // 기간별 조회 (통계용)
    List<Asset> findByUserAndAssetDateBetween(User user, LocalDate start, LocalDate end);

}