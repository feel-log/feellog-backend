package com.feellog.backend.domain.income.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public class IncomeResponseDto {
	public Long getIncomeId() {
		return incomeId;
	}
	public void setIncomeId(Long incomeId) {
		this.incomeId = incomeId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Long getIncomeCategoryId() {
		return incomeCategoryId;
	}
	public void setIncomeCategoryId(Long incomeCategoryId) {
		this.incomeCategoryId = incomeCategoryId;
	}
	public LocalDate getIncomeDate() {
		return incomeDate;
	}
	public void setIncomeDate(LocalDate incomeDate) {
		this.incomeDate = incomeDate;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	private Long incomeId;
	private BigDecimal amount;
	private Long incomeCategoryId;
	private LocalDate incomeDate;
	private String memo;
}
