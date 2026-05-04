package com.feellog.backend.domain.asset.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AssetCreateRequest {

    @NotNull(message = "자산 카테고리는 필수입니다.")
    private Long assetCategoryId;

    @NotNull(message = "자산 금액은 필수입니다.")
    @DecimalMin(value = "0", inclusive = true, message = "자산 금액은 0원 이상이어야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "자산 날짜는 필수입니다.")
    private LocalDate assetDate;

    @Size(max = 255, message = "메모는 255자 이하로 입력해주세요.")
    private String memo;
}