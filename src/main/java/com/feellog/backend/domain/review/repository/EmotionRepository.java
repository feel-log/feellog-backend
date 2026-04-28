package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.review.dto.EmotionOptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    @Query("""
        SELECT new com.feellog.backend.domain.review.dto.EmotionOptionResponse(
            e.id,
            eg.name,
            e.name
        )
        FROM Emotion e
        JOIN e.emotionGroup eg
        ORDER BY eg.id ASC, e.id ASC
    """)
    List<EmotionOptionResponse> findEmotionOptions();
}