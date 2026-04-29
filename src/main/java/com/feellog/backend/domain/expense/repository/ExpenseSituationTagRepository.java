package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseSituationTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSituationTagRepository extends JpaRepository<ExpenseSituationTag, Long> {

    List<ExpenseSituationTag> findByExpense(Expense expense);

    void deleteByExpense(Expense expense);
}