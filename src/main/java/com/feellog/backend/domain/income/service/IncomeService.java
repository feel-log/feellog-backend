package com.feellog.backend.domain.income.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.expense.dto.ExpenseResponseDto;
import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.income.dto.IncomeRequestDto;
import com.feellog.backend.domain.income.dto.IncomeResponseDto;
import com.feellog.backend.domain.income.entity.Income;
import com.feellog.backend.domain.income.entity.IncomeCategory;
import com.feellog.backend.domain.income.repository.IncomeCategoryRepository;
import com.feellog.backend.domain.income.repository.IncomeRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {
	private final IncomeRepository incomeRepository;
	private final IncomeCategoryRepository incomeCategoryRepository;
    private final UserRepository userRepository;

	
    @Transactional
	public Long createIncome(IncomeRequestDto dto, Long userId) {
		IncomeCategory incomeCategory = incomeCategoryRepository.findById(dto.getIncomeCategoryId())
				.orElseThrow(() -> new RuntimeException("카테고리 없음"));
		
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

		Income income = new Income();
		income.setUser(user);
		income.setIncomeCategory(incomeCategory);
		income.setAmount(dto.getAmount());
		income.setIncomeDate(dto.getIncomeDate());
		income.setMemo(dto.getMemo());
		income.setIsDeleted(false);
		income.setCreatedAt(LocalDateTime.now());
		income.setUpdatedAt(LocalDateTime.now());

		Income savedIncome = incomeRepository.save(income);
		
		return savedIncome.getId();
	}
    
    @Transactional
    public IncomeResponseDto getIncome(Long incomeId, Long userId) {
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

    	Income income = incomeRepository.findByIdAndIsDeletedFalse(incomeId)
    			.orElseThrow(() -> new RuntimeException("수입 없음"));
    	
    	if(!income.getUser().getId().equals(userId)) {
    		throw new RuntimeException("해당 지출에 대한 접근 권한이 없습니다.");
    	}
    	return toDto(income);
    }
    
    @Transactional
    public List<IncomeResponseDto> getMonthlyIncomes(int year, int month, Long userId) {
    	LocalDate start = LocalDate.of(year,  month,  1);
    	LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
    	
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

    	List<Income> list = incomeRepository.findByIncomeDateBetweenAndUserAndIsDeletedFalse(start, end, user);
    	
    	return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<IncomeResponseDto> getDailyIncomes(int year, int month, int day, Long userId) {
    	LocalDate date = LocalDate.of(year, month, day);
    	
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

    	List<Income> list = incomeRepository.findByIncomeDateAndUserAndIsDeletedFalse(date, user);

    	return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<IncomeResponseDto> getByCategory(Long categoryId, Long userId) {
        // Repository 메서드에 user, category 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
    	IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리 없음"));
    	
        List<Income> list = incomeRepository.findByUserAndIncomeCategoryAndIsDeletedFalse(user, category);
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public void updateIncome(Long incomeId, IncomeRequestDto dto, Long userId) {
    	Income income = incomeRepository.findById(incomeId)
    			.orElseThrow(() -> new RuntimeException("수입 없음"));
    	
    	if (!income.getUser().getId().equals(userId)) {
    		throw new RuntimeException("수정 권한이 없습니다.");
    	}
    	
    	IncomeCategory incomeCategory = incomeCategoryRepository.findById(dto.getIncomeCategoryId())
				.orElseThrow(() -> new RuntimeException("카테고리 없음"));

    	// Income 저장
		income.setIncomeCategory(incomeCategory);
		income.setAmount(dto.getAmount());
		income.setIncomeDate(dto.getIncomeDate());
		income.setMemo(dto.getMemo());
		income.setUpdatedAt(LocalDateTime.now());    	
    }
    
    @Transactional
    public void deleteIncome(Long incomeId, Long userId) {
    	Income income = incomeRepository.findById(incomeId)
    			.orElseThrow(() -> new RuntimeException("수업 없음"));
    	
    	if (!income.getUser().getId().equals(userId)) {
    		throw new RuntimeException("삭제 권한이 없습니다.");
    	}
    	
    	income.setIsDeleted(true);
    	income.setDeletedAt(LocalDateTime.now());
    }
    
    private IncomeResponseDto toDto(Income i) {
    	return IncomeResponseDto.builder()
    			.incomeId(i.getId())
    			.amount(i.getAmount())
    			.incomeCategoryId(i.getIncomeCategory().getId())
    			.incomeDate(i.getIncomeDate())
    			.memo(i.getMemo())
    			.build();
    }
       
}