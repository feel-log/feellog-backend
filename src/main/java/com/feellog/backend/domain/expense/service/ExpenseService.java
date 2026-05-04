package com.feellog.backend.domain.expense.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.category.repository.CategoryRepository;
import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.emotion.repository.EmotionRepository;
import com.feellog.backend.domain.expense.dto.ExpenseRequestDto;
import com.feellog.backend.domain.expense.dto.ExpenseResponseDto;
import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseEmotion;
import com.feellog.backend.domain.expense.entity.ExpenseSituationTag;
import com.feellog.backend.domain.expense.repository.ExpenseEmotionRepository;
import com.feellog.backend.domain.expense.repository.ExpenseRepository;
import com.feellog.backend.domain.expense.repository.ExpenseSituationTagRepository;
import com.feellog.backend.domain.paymentmethod.entity.PaymentMethod;
import com.feellog.backend.domain.paymentmethod.repository.PaymentMethodRepository;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
import com.feellog.backend.domain.situationtag.repository.SituationTagRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseEmotionRepository expenseEmotionRepository;
    private final ExpenseSituationTagRepository expenseSituationTagRepository;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;
    private final SituationTagRepository situationTagRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    
    @Transactional
    public Long createExpense(ExpenseRequestDto dto, Long userId) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리 없음"));
 
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
        		.orElseThrow(() -> new RuntimeException("결제 수단 없음"));
        // Expense 저장
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setPaymentMethod(paymentMethod);
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setExpenseTime(dto.getExpenseTime());
        expense.setMerchantName(dto.getMerchantName());
        expense.setMemo(dto.getMemo());
        expense.setIsDeleted(false);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        
        Expense savedExpense = expenseRepository.save(expense);
        
        // Emotion 저장
        if(dto.getEmotionIds() != null) {
            for (Long emotionId : dto.getEmotionIds()) {
                Emotion emotion = emotionRepository.findById(emotionId)
                        .orElseThrow(() -> new RuntimeException("감정 정보 없음"));
                
                ExpenseEmotion ee = new ExpenseEmotion();
                ee.setExpense(savedExpense);
                ee.setEmotion(emotion);
                ee.setCreatedAt(LocalDateTime.now());
                
                expenseEmotionRepository.save(ee);
            }
        }
        
        // Situation tag 저장
        if(dto.getSituationTagIds() != null) {
            for (Long situationId : dto.getSituationTagIds()) {
                SituationTag st = situationTagRepository.findById(situationId)
                        .orElseThrow(() -> new RuntimeException("상황 태그 정보 없음"));
                
                ExpenseSituationTag est = new ExpenseSituationTag();
                est.setExpense(savedExpense);
                est.setSituationTag(st);
                est.setCreatedAt(LocalDateTime.now());
                
                expenseSituationTagRepository.save(est);
            }
        }
        
        return savedExpense.getId();
    }
    
    @Transactional
    public ExpenseResponseDto getExpense(Long expenseId, Long userId) {
        // 본인의 지출이고 삭제되지 않은 것만 조회하도록 검증 로직 추가
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

        Expense expense = expenseRepository.findByIdAndIsDeletedFalse(expenseId)
                .orElseThrow(() -> new RuntimeException("지출 없음"));
        
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 지출에 대한 접근 권한이 없습니다.");
        }
        
        return toDto(expense);
    }
    
    @Transactional
    public List<ExpenseResponseDto> getMonthlyExpenses(int year, int month, Long userId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        
        // Repository 메서드에 user, start date, end date 파라미터 추가
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
        List<Expense> list = expenseRepository.findByExpenseDateBetweenAndUserAndIsDeletedFalse(start, end, user);
    
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<ExpenseResponseDto> getDailyExpenses(int year, int month, int day, Long userId) {
        LocalDate date = LocalDate.of(year, month, day);
        
        // Repository 메서드에 user, start date, end date 파라미터 추가
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
        List<Expense> list = expenseRepository.findByExpenseDateAndUserAndIsDeletedFalse(date, user);
    
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<ExpenseResponseDto> getByCategory(Long categoryId, Long userId) {
        // Repository 메서드에 user, category 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
    	Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리 없음"));
    	
        List<Expense> list = expenseRepository.findByUserAndCategoryAndIsDeletedFalse(user, category);
        return list.stream().map(this::toDto).toList();
    }
   
    @Transactional
    public List<ExpenseResponseDto> getExpensesByCategoryGroup(Long groupId, Long userId) {
        // Repository 메서드에 user, groupId 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
    	List<Expense> list = expenseRepository.findByCategory_CategoryGroup_IdAndUserAndIsDeletedFalse(groupId, user);
        return list.stream().map(this::toDto).toList();
    }
        
    @Transactional
    public List<ExpenseResponseDto> getByEmotion(Long emotionId, Long userId) {
        // Repository 메서드에 user, Emotion 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
    	Emotion emotion = emotionRepository.findById(emotionId)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
    	List<Expense> list = expenseEmotionRepository.findExpensesByEmotionAndUser(emotion, user);
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<ExpenseResponseDto> getExpensesByEmotionGroup(Long groupId, Long userId) {
        // Repository 메서드에 user, groupId 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));
        
    	List<Expense> list = expenseEmotionRepository.findExpensesByEmotionGroup(groupId, user);
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public List<ExpenseResponseDto> getBySituationTag(Long situationTagId, Long userId) {
        // Repository 메서드에 user, situationTag 파라미터 추가 필요
    	User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("활성 유저 없음"));

        List<Expense> list = expenseSituationTagRepository.findByExpenseByUserAndSituationTag(situationTagId, user);
        return list.stream().map(this::toDto).toList();
    }
    
    @Transactional
    public void updateExpense(Long expenseId, ExpenseRequestDto dto, Long userId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출 없음"));

        // 권한 체크
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리 없음"));
         
        expense.setCategory(category);
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setExpenseTime(dto.getExpenseTime());
        expense.setMerchantName(dto.getMerchantName());
        expense.setMemo(dto.getMemo());
        expense.setUpdatedAt(LocalDateTime.now());
        
        
        // 연관 데이터(Emotion, Tag) 갱신 로직
        expenseEmotionRepository.deleteByExpense(expense);
        expenseEmotionRepository.flush();
        
        if (dto.getEmotionIds() != null) {
            List<ExpenseEmotion> list = new ArrayList<>();
            for (Long emotionId : dto.getEmotionIds()) {
                Emotion emotion = emotionRepository.findById(emotionId)
                        .orElseThrow(() -> new RuntimeException("감정 정보 없음"));
                ExpenseEmotion ee = new ExpenseEmotion();
                ee.setExpense(expense);
                ee.setEmotion(emotion);
                ee.setCreatedAt(LocalDateTime.now());
                list.add(ee);
            }
            expenseEmotionRepository.saveAll(list);
        }
        
        expenseSituationTagRepository.deleteByExpense(expense);
        expenseSituationTagRepository.flush();
        
        if (dto.getSituationTagIds() != null) {
            List<ExpenseSituationTag> list = new ArrayList<>();
            for (Long situationId : dto.getSituationTagIds()) {
                SituationTag situationTag = situationTagRepository.findById(situationId)
                        .orElseThrow(() -> new RuntimeException("상황 태그 정보 없음"));
                ExpenseSituationTag st = new ExpenseSituationTag();
                st.setExpense(expense);
                st.setSituationTag(situationTag);
                st.setCreatedAt(LocalDateTime.now());
                list.add(st);
            }
            expenseSituationTagRepository.saveAll(list);
        }
    }
    
    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출 없음"));
        
        // 권한 체크
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        
        expense.setIsDeleted(true);
        expense.setDeletedAt(LocalDateTime.now());
    }
    
    private ExpenseResponseDto toDto(Expense e) {
        List<Long> emotionIds = expenseEmotionRepository
                .findByExpense(e)
                .stream()
                .map(ee -> ee.getEmotion().getId())
                .toList();
        
        List<Long> situationTagIds = expenseSituationTagRepository
                .findByExpense(e)
                .stream()
                .map(st -> st.getSituationTag().getId())
                .toList();
        
        return ExpenseResponseDto.builder()
                .expenseId(e.getId())
                .categoryId(e.getCategory().getId())
                .categoryGroupId(e.getCategory().getCategoryGroup().getId())
                .paymentMethodId(e.getPaymentMethod().getId())
                .amount(e.getAmount())
                .memo(e.getMemo())
                .merchantName(e.getMerchantName())
                .expenseDate(e.getExpenseDate())
                .expenseTime(e.getExpenseTime())
                .emotionIds(emotionIds)
                .situationTagIds(situationTagIds)
                .build();
    }
}