package com.feellog.backend.domain.catalog.emotion.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emotion_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_group_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}