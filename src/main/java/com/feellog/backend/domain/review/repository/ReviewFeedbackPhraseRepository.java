package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewFeedbackPhrase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewFeedbackPhraseRepository extends JpaRepository<ReviewFeedbackPhrase, Long> {

    Optional<ReviewFeedbackPhrase> findBySituationGroupCodeAndSatisfactionGroupCode(
            String situationGroupCode,
            String satisfactionGroupCode
    );
}