package com.feellog.backend.domain.asset.repository;

import com.feellog.backend.domain.asset.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {

    Optional<AssetCategory> findByName(String name);

}