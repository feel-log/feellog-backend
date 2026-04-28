package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewResultTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewResultTemplateRepository extends JpaRepository<ReviewResultTemplate, Long> {

    Optional<ReviewResultTemplate>
    findFirstByEmotionIdAndSituationTagIdAndSatisfactionOptionIdAndNextActionOptionIdAndIsActiveTrueOrderByPriorityAsc(
            Long emotionId,
            Long situationTagId,
            Long satisfactionOptionId,
            Long nextActionOptionId
    );
}