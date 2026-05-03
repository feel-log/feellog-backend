package com.feellog.backend.domain.report.repository;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.report.dto.CategoryAmountDto;
import com.feellog.backend.domain.report.dto.projection.CategoryExpenseSummary;
import com.feellog.backend.domain.report.dto.projection.EmotionSummary;
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

    @Query("""
        SELECT e FROM Expense e
        JOIN FETCH e.category c
        JOIN FETCH e.paymentMethod p
        JOIN e.expenseEmotions ee
        WHERE e.user.id = :userId
        AND ee.emotion.id = :emotionId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
    """)
    Page<Expense> findExpensesByEmotionAndPeriod(
            @Param("userId") Long userId,
            @Param("emotionId") Long emotionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
        SELECT SUM(e.amount) FROM Expense e
        JOIN e.expenseEmotions ee
        WHERE e.user.id = :userId
        AND ee.emotion.id = :emotionId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        AND e.isDeleted = false
    """)
    BigDecimal findTotalAmountByEmotionAndPeriod(
            @Param("userId") Long userId,
            @Param("emotionId") Long emotionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 카테고리별 집계 (동률 정렬 포함)
    @Query("""
        SELECT e.category.id           AS categoryId,
               e.category.name         AS name,
               SUM(e.amount)           AS total,
               MIN(e.expenseTime)      AS firstTime,
               MAX(e.createdAt)        AS lastCreatedAt
        FROM Expense e
        WHERE e.user.id = :userId
          AND e.expenseDate = :date
          AND e.isDeleted = false
        GROUP BY e.category.id, e.category.name
        ORDER BY total DESC, CASE WHEN MIN(e.expenseTime) IS NULL THEN 1 ELSE 0 END ASC, firstTime ASC, lastCreatedAt DESC, e.category.id ASC
    """)
    List<CategoryExpenseSummary> findCategoryExpenseSummaryByDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    // 감정 집계
    @Query("""
        SELECT ee.emotion.id                AS emotionId,
               ee.emotion.name              AS name,
               ee.emotion.emotionGroup.name AS emotionGroupName,
               COUNT(ee.expense.id)         AS emotionCount,
               SUM(ee.expense.amount)       AS linkedAmount,
               MAX(ee.createdAt)            AS lastUsedAt
        FROM ExpenseEmotion ee
        WHERE ee.expense.user.id = :userId
          AND ee.expense.expenseDate = :date
          AND ee.expense.isDeleted = false
        GROUP BY ee.emotion.id, ee.emotion.name, ee.emotion.emotionGroup.name
        ORDER BY emotionCount DESC, linkedAmount DESC, lastUsedAt DESC, ee.emotion.id ASC
    """)
    List<EmotionSummary> findEmotionSummaryByDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    // 총 지출 합계
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
          AND e.expenseDate = :date
          AND e.isDeleted = false
    """)
    BigDecimal findTotalExpenseByDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}
