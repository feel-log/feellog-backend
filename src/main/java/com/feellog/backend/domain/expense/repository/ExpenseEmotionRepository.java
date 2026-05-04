package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseEmotion;
import com.feellog.backend.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseEmotionRepository extends JpaRepository<ExpenseEmotion, Long> {

    List<ExpenseEmotion> findByExpense(Expense expense);
    
    @Query("""
    	SELECT ee.expense
    	FROM ExpenseEmotion ee
    	WHERE ee.emotion = :emotion
    	AND ee.expense.user = :user
    		""")
    List<Expense> findExpensesByEmotionAndUser(@Param("emotion")Emotion emotion, @Param("user")User user);

    @Query("""
    	SELECT ee.expense
    	FROM ExpenseEmotion ee
    	WHERE ee.emotion.emotionGroup.id = :groupId
    	AND ee.expense.user = :user
    		""")
    List<Expense> findExpensesByEmotionGroup(@Param("groupId")Long groupId, @Param("user")User user);
    void deleteByExpense(Expense expense);
}