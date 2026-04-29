package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseEmotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseEmotionRepository extends JpaRepository<ExpenseEmotion, Long> {

    List<ExpenseEmotion> findByExpense(Expense expense);

    void deleteByExpense(Expense expense);
}