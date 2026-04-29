package com.feellog.backend.domain.category.repository;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.category.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCategoryGroup(CategoryGroup categoryGroup);

    Optional<Category> findByName(String name);

}