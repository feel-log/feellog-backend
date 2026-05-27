package com.feellog.backend.domain.expense.controller;


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

import com.feellog.backend.domain.expense.dto.ExpenseRequestDto;
import com.feellog.backend.domain.expense.dto.ExpenseResponseDto;
import com.feellog.backend.domain.expense.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {
	private final ExpenseService expenseService;
	
	// expense 생성
	@PostMapping
	public ResponseEntity<Map<String, Object>> createExpense(@RequestBody ExpenseRequestDto dto,
											 				 @AuthenticationPrincipal Long userId) {
		
		dto.setUserId(userId);
		Long expenseId = expenseService.createExpense(dto, userId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
				Map.of(
				"message", "지출 생성 완료",
				"expenseId", expenseId
				)
		);
	}
	
	// expense 조회
	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponseDto> getExpense(@PathVariable("expenseId") Long expenseId,
										 @AuthenticationPrincipal Long userId) {
		ExpenseResponseDto response = expenseService.getExpense(expenseId, userId);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/daily")
	public ResponseEntity<List<ExpenseResponseDto>> getDailyExpenses(
			@RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("day") int day,
			@AuthenticationPrincipal Long userId) 
	{
		List<ExpenseResponseDto> responses = expenseService.getDailyExpenses(year, month, day, userId);
		
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/monthly")
	public ResponseEntity<List<ExpenseResponseDto>> getMonthlyExpenses(
			@RequestParam("year") int year, @RequestParam("month") int month,
			@AuthenticationPrincipal Long userId) 
	{
		List<ExpenseResponseDto> responses = expenseService.getMonthlyExpenses(year, month, userId);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ExpenseResponseDto>> getByCategory(@PathVariable("categoryId") Long categoryId,
												  				  @AuthenticationPrincipal Long userId) {
		List<ExpenseResponseDto> responses = expenseService.getByCategory(categoryId, userId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/category_group/{groupId}")
	public ResponseEntity<List<ExpenseResponseDto>> getByCategoryGroup(@PathVariable("groupId") Long groupId,
													   				   @AuthenticationPrincipal Long userId) {
		List<ExpenseResponseDto> responses = expenseService.getExpensesByCategoryGroup(groupId, userId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/emotion/{emotionId}")
	public ResponseEntity<List<ExpenseResponseDto>> getByEmotion(@PathVariable("emotionId") Long emotionId,
			     								 @AuthenticationPrincipal Long userId) {
		List<ExpenseResponseDto> responses = expenseService.getByEmotion(emotionId, userId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/emotion_group/{groupId}")
	public ResponseEntity<List<ExpenseResponseDto>> getByEmotionGroup(@PathVariable("groupId") Long groupId,
			                                          @AuthenticationPrincipal Long userId) {
		List<ExpenseResponseDto> responses = expenseService.getExpensesByEmotionGroup(groupId, userId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/situation_tag/{situationTagId}")
	public ResponseEntity<List<ExpenseResponseDto>> getBySituation(@PathVariable("situationTagId") Long situationTagId,
			                                       @AuthenticationPrincipal Long userId) {
		List<ExpenseResponseDto> responses = expenseService.getBySituationTag(situationTagId, userId);
		return ResponseEntity.ok(responses);
	}
	
	// expense 수정
	@PutMapping("/{expenseId}")
	public ResponseEntity<Map<String, Object>> updateExpense(@PathVariable("expenseId") Long expenseId, 
			                                 @RequestBody ExpenseRequestDto dto,
			                                 @AuthenticationPrincipal Long userId) {
		expenseService.updateExpense(expenseId, dto, userId);
		return ResponseEntity.ok(Map.of("message", "지출 수정 완료"));
	}
	
	// expense 삭제
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<?> deleteExpense(@PathVariable("expenseId") Long expenseId,
			                               @AuthenticationPrincipal Long userId) {
		expenseService.deleteExpense(expenseId, userId);
		
		return ResponseEntity.ok(Map.of("message", "지출 삭제 완료"));
	}
	
}
