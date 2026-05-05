package com.feellog.backend.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "review_emotion_adjustment_phrase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_emotion_adjustment_phrase",
                        columnNames = {"emotion_group_code"}
                )
        }
)
public class ReviewEmotionAdjustmentPhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_emotion_adjustment_phrase_id")
    private Long id;

    @Column(name = "emotion_group_code", nullable = false, length = 50)
    private String emotionGroupCode;

    @Column(name = "adjustment_text", columnDefinition = "TEXT", nullable = false)
    private String adjustmentText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}