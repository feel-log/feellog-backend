package com.feellog.backend.domain.income.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feellog.backend.domain.expense.dto.ExpenseResponseDto;
import com.feellog.backend.domain.income.dto.IncomeRequestDto;
import com.feellog.backend.domain.income.dto.IncomeResponseDto;
import com.feellog.backend.domain.income.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {
	private final IncomeService incomeService;
	
	// income 생성
	@PostMapping
	public ResponseEntity<Map<String, Object>> createIncome(@RequestBody IncomeRequestDto dto, 
											@AuthenticationPrincipal Long userId) {
		dto.setUserId(userId);
		Long incomeId = incomeService.createIncome(dto, userId);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of(
				"message", "수입 생성 완료", 
				"IncomeId", incomeId
				)
		);
	}
	
	// income 조회
	@GetMapping("/{incomeId}")
	public ResponseEntity<IncomeResponseDto> getIncome(@PathVariable("incomeId") Long incomeId,
									   @AuthenticationPrincipal Long userId) {
		IncomeResponseDto response = incomeService.getIncome(incomeId,  userId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/daily")
	public ResponseEntity<List<IncomeResponseDto>> getDailyIncome(
			@RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("day") int day,
			@AuthenticationPrincipal Long userId) {
		
		List<IncomeResponseDto> response = incomeService.getDailyIncomes(year, month, day, userId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/monthly")
	public ResponseEntity<List<IncomeResponseDto>> getMonthlyIncome(
			@RequestParam("year") int year, @RequestParam("month") int month,
			 @AuthenticationPrincipal Long userId) {
		
		List<IncomeResponseDto> responses = incomeService.getMonthlyIncomes(year, month, userId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<IncomeResponseDto>> getByCategory(@PathVariable("categoryId") Long categoryId,
												  @AuthenticationPrincipal Long userId) {
		
		List<IncomeResponseDto> responses = incomeService.getByCategory(categoryId, userId);
		return ResponseEntity.ok(responses);
	}
	
	// income 수정
	@PutMapping("/{incomeId}")
	public ResponseEntity<Map<String, Object>> updateIncome(
			@PathVariable("incomeId") Long incomeId, @RequestBody IncomeRequestDto dto,
			@AuthenticationPrincipal Long userId) {
		
		incomeService.updateIncome(incomeId,  dto,  userId);
		return ResponseEntity.ok(Map.of("message", "수입 수정 완료"));
	}
	
	// income 삭제
	@DeleteMapping("/{incomeId}")
	public ResponseEntity<Map<String, Object>> deleteIncome(
			@PathVariable("incomeId") Long incomeId,
			@AuthenticationPrincipal Long userId) {
		
		incomeService.deleteIncome(incomeId, userId);
		return ResponseEntity.ok(Map.of("message", "수입 삭제 완료"));
	}
	
}
