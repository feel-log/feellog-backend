package com.feellog.backend.domain.income.repository;

import com.feellog.backend.domain.income.entity.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

    Optional<IncomeCategory> findByName(String name);

}