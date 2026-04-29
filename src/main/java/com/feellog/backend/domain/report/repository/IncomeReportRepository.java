package com.feellog.backend.domain.report.repository;

import com.feellog.backend.domain.income.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncomeReportRepository extends JpaRepository<Income, Long> {

    @Query("""
        SELECT i FROM Income i
        WHERE i.user.id = :userId
        AND i.incomeDate BETWEEN :startDate AND :endDate
        AND i.isDeleted = false
    """)
    List<Income> findIncomesByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}