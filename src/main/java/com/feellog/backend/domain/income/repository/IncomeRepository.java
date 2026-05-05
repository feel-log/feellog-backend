package com.feellog.backend.domain.income.repository;

import com.feellog.backend.domain.income.entity.Income;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    // 사용자 기준 전체 수입 조회
    List<Income> findByUser(User user);

    // 특정 날짜 수입 조회
    List<Income> findByUserAndIncomeDate(User user, LocalDate incomeDate);

    // 기간별 조회 (통계용)
    List<Income> findByUserAndIncomeDateBetween(User user, LocalDate start, LocalDate end);

}