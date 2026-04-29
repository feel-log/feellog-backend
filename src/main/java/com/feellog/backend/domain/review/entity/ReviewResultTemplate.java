package com.feellog.backend.domain.review.entity;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.catalog.situationtag.entity.SituationTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review_result_template",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_result_template",
                        columnNames = {
                                "emotion_id",
                                "situation_tag_id",
                                "satisfaction_option_id",
                                "next_action_option_id"
                        }
                )
        }
)
public class ReviewResultTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_result_template_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situation_tag_id", nullable = false)
    private SituationTag situationTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satisfaction_option_id", nullable = false)
    private ReviewChoiceOption satisfactionOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_action_option_id", nullable = false)
    private ReviewChoiceOption nextActionOption;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @Column(nullable = false, length = 150)
    private String feedbackTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Column(nullable = false, length = 150)
    private String guideTitle;

    @Column(name = "guide_item_1", nullable = false, length = 255)
    private String guideItem1;

    @Column(name = "guide_item_2", nullable = false, length = 255)
    private String guideItem2;

    @Column(name = "guide_item_3", nullable = false, length = 255)
    private String guideItem3;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}