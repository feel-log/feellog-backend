package com.feellog.backend.domain.asset.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AssetUpdateRequest {

    @NotNull(message = "자산 카테고리는 필수입니다.")
    private Long assetCategoryId;

    @NotNull(message = "자산 금액은 필수입니다.")
    @DecimalMin(value = "0", message = "자산 금액은 0원 이상이어야 합니다.")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "자산 날짜는 필수입니다.")
    private LocalDate assetDate;

    private String memo;
}