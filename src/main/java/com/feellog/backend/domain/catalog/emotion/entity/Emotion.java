package com.feellog.backend.domain.catalog.emotion.entity;

import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emotion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emotion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_group_id", nullable = false)
    private EmotionGroup emotionGroup;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}