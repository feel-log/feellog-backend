package com.feellog.backend.domain.emotion.repository;

import com.feellog.backend.domain.emotion.entity.EmotionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmotionGroupRepository extends JpaRepository<EmotionGroup, Long> {

    Optional<EmotionGroup> findByName(String name);
    
    @Query("""
    		SELECT eg FROM EmotionGroup eg
    		LEFT JOIN FETCH eg.emotions
    """)
    List<EmotionGroup> findAllWithEmotions();

}