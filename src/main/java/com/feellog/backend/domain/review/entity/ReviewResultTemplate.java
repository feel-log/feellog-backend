package com.feellog.backend.domain.review.entity;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.catalog.situationtag.entity.SituationTag;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "review_result_template")
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

    @Column(nullable = false)
    private String title;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "feedback_title", nullable = false)
    private String feedbackTitle;

    @Column(name = "feedback_text", nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Column(name = "guide_title", nullable = false)
    private String guideTitle;

    @Column(name = "guide_item_1", nullable = false)
    private String guideItem1;

    @Column(name = "guide_item_2", nullable = false)
    private String guideItem2;

    @Column(name = "guide_item_3", nullable = false)
    private String guideItem3;

    private Integer priority;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}