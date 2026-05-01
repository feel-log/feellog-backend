package com.feellog.backend.domain.report.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.report.dto.CategoryAmountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
        SELECT new com.feellog.backend.domain.report.dto.CategoryAmountDto(e.category.id, SUM(e.amount))
        FROM Expense e
        WHERE e.user.id = :userId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
        GROUP BY e.category.id
    """)
    List<CategoryAmountDto> findCategoryAmountByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT e FROM Expense e
        WHERE e.user.id = :userId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
    """)
    List<Expense> findExpensesOnlyByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT e FROM Expense e
        JOIN FETCH e.category c
        JOIN FETCH e.paymentMethod p
        WHERE e.user.id = :userId
        AND e.category.id = :categoryId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
    """)

    Page<Expense> findExpensesByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
        SELECT SUM(e.amount) FROM Expense e
        WHERE e.user.id = :userId
        AND e.category.id = :categoryId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
    """)
    BigDecimal findTotalAmountByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
