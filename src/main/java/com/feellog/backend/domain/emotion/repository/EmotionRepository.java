package com.feellog.backend.domain.emotion.repository;

import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.emotion.entity.EmotionGroup;
import com.feellog.backend.domain.review.dto.EmotionOptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    // 그룹별 감정 조회
    List<Emotion> findByEmotionGroup(EmotionGroup emotionGroup);

    Optional<Emotion> findByName(String name);

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