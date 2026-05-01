package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.dto.ReviewChoiceOptionResponse;
import com.feellog.backend.domain.review.entity.ReviewChoiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewChoiceOptionRepository extends JpaRepository<ReviewChoiceOption, Long> {

    @Query("""
        SELECT new com.feellog.backend.domain.review.dto.ReviewChoiceOptionResponse(
            r.id,
            r.optionText
        )
        FROM ReviewChoiceOption r
        WHERE r.questionType = :questionType
          AND r.isActive = true
        ORDER BY r.sortOrder ASC
    """)
    List<ReviewChoiceOptionResponse> findOptionsByQuestionType(String questionType);
}