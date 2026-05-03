package com.feellog.backend.domain.asset.entity;

import com.feellog.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_category_id", nullable = false)
    private AssetCategory assetCategory;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate assetDate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Asset create(User user, AssetCategory assetCategory, BigDecimal amount, LocalDate assetDate, String memo) {
        Asset asset = new Asset();
        asset.user = user;
        asset.assetCategory = assetCategory;
        asset.amount = amount;
        asset.assetDate = assetDate;
        asset.memo = memo;
        asset.isDeleted = false;
        asset.createdAt = LocalDateTime.now();
        asset.updatedAt = LocalDateTime.now();
        return asset;
    }

    public void update(AssetCategory assetCategory, BigDecimal amount, LocalDate assetDate, String memo) {
        this.assetCategory = assetCategory;
        this.amount = amount;
        this.assetDate = assetDate;
        this.memo = memo;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}