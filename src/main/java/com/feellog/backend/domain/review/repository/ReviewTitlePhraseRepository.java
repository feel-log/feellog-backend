package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewTitlePhrase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewTitlePhraseRepository extends JpaRepository<ReviewTitlePhrase, Long> {

    Optional<ReviewTitlePhrase> findBySituationGroupCodeAndSatisfactionGroupCode(
            String situationGroupCode,
            String satisfactionGroupCode
    );
}