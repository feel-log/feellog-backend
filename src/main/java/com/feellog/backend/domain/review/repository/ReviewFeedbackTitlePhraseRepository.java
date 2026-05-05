package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewFeedbackTitlePhrase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewFeedbackTitlePhraseRepository extends JpaRepository<ReviewFeedbackTitlePhrase, Long> {

    Optional<ReviewFeedbackTitlePhrase> findBySituationGroupCode(String situationGroupCode);
}