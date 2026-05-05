package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.ReviewPhraseGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewPhraseGroupMappingRepository extends JpaRepository<ReviewPhraseGroupMapping, Long> {

    Optional<ReviewPhraseGroupMapping> findBySourceTypeAndSourceId(String sourceType, Long sourceId);
}