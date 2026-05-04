package com.feellog.backend.domain.expense.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseSituationTag;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
import com.feellog.backend.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseSituationTagRepository extends JpaRepository<ExpenseSituationTag, Long> {

    List<ExpenseSituationTag> findByExpense(Expense expense);

    @Query("""
    	SELECT est.expense
    	FROM ExpenseSituationTag est
    	WHERE est.situationTag.id = :situationTagId
    	AND est.expense.user = :user
    		""")
    List<Expense> findByExpenseByUserAndSituationTag(@Param("situationTagId")Long situationTagId, @Param("user")User user);
    void deleteByExpense(Expense expense);
}