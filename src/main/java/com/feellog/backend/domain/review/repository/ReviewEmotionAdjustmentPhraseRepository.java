package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewEmotionAdjustmentPhrase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewEmotionAdjustmentPhraseRepository extends JpaRepository<ReviewEmotionAdjustmentPhrase, Long> {

    Optional<ReviewEmotionAdjustmentPhrase> findByEmotionGroupCode(String emotionGroupCode);
}