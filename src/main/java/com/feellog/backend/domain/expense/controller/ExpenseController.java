package com.feellog.backend.domain.expense.controller;


import java.util.List;
import java.util.Map;

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
	public Map<String, Object> createExpense(@RequestBody ExpenseRequestDto dto,
											 @AuthenticationPrincipal Long userId) {
		//System.out.println("#####################################################################");
		//System.out.println(dto.getUserId());
		//System.out.println(dto.getCategoryId());
		//System.out.println(dto.getAmount());
		//System.out.println(dto.getMemo());
		//System.out.println(dto.getMerchantName());
		//System.out.println(dto.getExpenseDate());
		//System.out.println(dto.getExpenseTime());
		
		dto.setUserId(userId);
		//System.out.println("Authenticated User ID from Token: " + userId);

		Long expenseId = expenseService.createExpense(dto, userId);
		
		return Map.of(
				"message", "지출 생성 완료",
				"expenseId", expenseId
		);
	}
	
	// expense 조회
	@GetMapping("/{expenseId}")
	public ExpenseResponseDto getExpense(@PathVariable("expenseId") Long expenseId,
										 @AuthenticationPrincipal Long userId) {
	    return expenseService.getExpense(expenseId, userId);
	}
	
	@GetMapping("/daily")
	public List<ExpenseResponseDto> getDailyExpenses(
			@RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("day") int day,
			@AuthenticationPrincipal Long userId) 
	{
		return expenseService.getDailyExpenses(year, month, day, userId);
	}
	
	@GetMapping("/monthly")
	public List<ExpenseResponseDto> getMonthlyExpenses(
			@RequestParam("year") int year, @RequestParam("month") int month,
			@AuthenticationPrincipal Long userId) 
	{
		return expenseService.getMonthlyExpenses(year, month, userId);
	}
	

	@GetMapping("/category/{categoryId}")
	public List<ExpenseResponseDto> getByCategory(@PathVariable("categoryId") Long categoryId,
												  @AuthenticationPrincipal Long userId) {
		return expenseService.getByCategory(categoryId, userId);
	}
	
	@GetMapping("/category_group/{groupId}")
	public List<ExpenseResponseDto> getByCategoryGroup(@PathVariable("groupId") Long groupId,
													   @AuthenticationPrincipal Long userId) {
		return expenseService.getExpensesByCategoryGroup(groupId, userId);
	}
	
	@GetMapping("/emotion/{emotionId}")
	public List<ExpenseResponseDto> getByEmotion(@PathVariable("emotionId") Long emotionId,
			     								 @AuthenticationPrincipal Long userId) {
		return expenseService.getByEmotion(emotionId, userId);
	}
	
	@GetMapping("/emotion_group/{groupId}")
	public List<ExpenseResponseDto> getByEmotionGroup(@PathVariable("groupId") Long groupId,
			                                          @AuthenticationPrincipal Long userId) {
		return expenseService.getExpensesByEmotionGroup(groupId, userId);
	}
	
	@GetMapping("/situation_tag/{situationTagId}")
	public List<ExpenseResponseDto> getBySituation(@PathVariable("situationTagId") Long situationTagId,
			                                       @AuthenticationPrincipal Long userId) {
		return expenseService.getBySituationTag(situationTagId, userId);
	}
	
	// expense 수정
	@PutMapping("/{expenseId}")
	public Map<String, Object> updateExpense(@PathVariable("expenseId") Long expenseId, 
			                                 @RequestBody ExpenseRequestDto dto,
			                                 @AuthenticationPrincipal Long userId) {
		expenseService.updateExpense(expenseId, dto, userId);
		return Map.of("message", "지출 수정 완료");
	}
	
	// expense 삭제
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<?> deleteExpense(@PathVariable("expenseId") Long expenseId,
			                               @AuthenticationPrincipal Long userId) {
		expenseService.deleteExpense(expenseId, userId);
		
		return ResponseEntity.ok(Map.of("message", "지출 삭제 완료"));
	}
	
}
