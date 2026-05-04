package com.feellog.backend.domain.category.repository;

import com.feellog.backend.domain.category.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

    Optional<CategoryGroup> findByName(String name);
    
    @Query("""
    	SELECT cg FROM CategoryGroup cg
    	LEFT JOIN FETCH cg.categories
    """)
    List<CategoryGroup> findAllWithCategories();

}