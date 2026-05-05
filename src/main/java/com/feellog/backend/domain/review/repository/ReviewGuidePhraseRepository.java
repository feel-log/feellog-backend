package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewGuidePhrase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewGuidePhraseRepository extends JpaRepository<ReviewGuidePhrase, Long> {

    Optional<ReviewGuidePhrase> findByNextActionGroupCode(String nextActionGroupCode);
}