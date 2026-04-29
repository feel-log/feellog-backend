package com.feellog.backend.domain.report.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Expense, Long> {

    @Query("""
    SELECT e FROM Expense e
    JOIN FETCH e.category c
    JOIN FETCH c.categoryGroup
    WHERE e.user.id = :userId
    AND e.expenseDate BETWEEN :startDate AND :endDate
    AND e.isDeleted = false
""")
    List<Expense> findExpensesByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
