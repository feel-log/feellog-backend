package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserAndIsDeletedFalse(User user);

    List<Expense> findByUserAndExpenseDateAndIsDeletedFalse(User user, LocalDate expenseDate);

    List<Expense> findByUserAndExpenseDateBetweenAndIsDeletedFalse(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
}