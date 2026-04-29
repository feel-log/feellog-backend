package com.feellog.backend.domain.emotion.repository;

import com.feellog.backend.domain.emotion.entity.EmotionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionGroupRepository extends JpaRepository<EmotionGroup, Long> {

    Optional<EmotionGroup> findByName(String name);

}