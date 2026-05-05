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
        name = "review_feedback_phrase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_feedback_phrase",
                        columnNames = {"situation_group_code", "satisfaction_group_code"}
                )
        }
)
public class ReviewFeedbackPhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_feedback_phrase_id")
    private Long id;

    @Column(name = "situation_group_code", nullable = false, length = 50)
    private String situationGroupCode;

    @Column(name = "satisfaction_group_code", nullable = false, length = 50)
    private String satisfactionGroupCode;

    @Column(name = "feedback_text", columnDefinition = "TEXT", nullable = false)
    private String feedbackText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}