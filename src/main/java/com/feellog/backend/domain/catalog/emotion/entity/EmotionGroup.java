package com.feellog.backend.domain.catalog.emotion.entity;

import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emotion_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_group_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "emotionGroup")
    private List<Emotion> emotions = new ArrayList<>();
}