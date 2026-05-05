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
        name = "review_feedback_title_phrase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_feedback_title_phrase",
                        columnNames = {"situation_group_code"}
                )
        }
)
public class ReviewFeedbackTitlePhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_feedback_title_phrase_id")
    private Long id;

    @Column(name = "situation_group_code", nullable = false, length = 50)
    private String situationGroupCode;

    @Column(name = "feedback_title", nullable = false, length = 255)
    private String feedbackTitle;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}