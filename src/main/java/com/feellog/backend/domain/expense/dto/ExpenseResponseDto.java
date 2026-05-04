package com.feellog.backend.domain.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Builder;

@Builder
public class ExpenseResponseDto {
	private Long expenseId;
	private Long categoryId;
	private Long categoryGroupId;
	
	private Long paymentMethodId;
	private BigDecimal amount;
	private String memo;
	private String merchantName;
	private LocalDate expenseDate;
	private LocalTime expenseTime;
	
	private List<Long> emotionIds;
	private List<Long> situationTagIds;
	
	public Long getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getPaymentMethodId() {
		return paymentMethodId;
	}
	public void setPaymentMethodId(Long paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public LocalDate getExpenseDate() {
		return expenseDate;
	}
	public void setExpenseDate(LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}
	public List<Long> getEmotionIds() {
		return emotionIds;
	}
	public void setEmotionIds(List<Long> emotionIds) {
		this.emotionIds = emotionIds;
	}
	public List<Long> getSituationTagIds() {
		return situationTagIds;
	}
	public void setSituationTagIds(List<Long> situationTagIds) {
		this.situationTagIds = situationTagIds;
	}
	public LocalTime getExpenseTime() {
		return expenseTime;
	}
	public void setExpenseTime(LocalTime expenseTime) {
		this.expenseTime = expenseTime;
	}
	public Long getCategoryGroupId() {
		return categoryGroupId;
	}
	public void setCategoryGroupId(Long categoryGroupId) {
		this.categoryGroupId = categoryGroupId;
	}



}
