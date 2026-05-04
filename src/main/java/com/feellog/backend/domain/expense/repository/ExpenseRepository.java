package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserAndIsDeletedFalse(User user);
    
    Optional<Expense> findByIdAndIsDeletedFalse(Long id);

    List<Expense> findByUserAndExpenseDateAndIsDeletedFalse(User user, LocalDate expenseDate);

    List<Expense> findByUserAndExpenseDateBetweenAndIsDeletedFalse(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findByExpenseDateAndUserAndIsDeletedFalse(LocalDate expenseDate, User user);

    List<Expense> findByExpenseDateBetweenAndUserAndIsDeletedFalse(
            LocalDate startDate,
            LocalDate endDate,
            User user
    );
    
    
    List<Expense> findByUserAndCategoryAndIsDeletedFalse(User user, Category category);
    
    List<Expense> findByCategory_CategoryGroup_IdAndUserAndIsDeletedFalse(
    	    Long categoryGroupId,
    	    User user
    	);

}