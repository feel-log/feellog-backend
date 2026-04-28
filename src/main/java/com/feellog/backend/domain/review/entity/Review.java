package com.feellog.backend.domain.review.entity;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.catalog.situationtag.entity.SituationTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_user_date",
                        columnNames = {"user_id", "review_date"}
                )
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_result_template_id")
    private ReviewResultTemplate reviewResultTemplate;

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Review(
            Long userId,
            LocalDate reviewDate,
            Emotion emotion,
            SituationTag situationTag,
            ReviewChoiceOption satisfactionOption,
            ReviewChoiceOption nextActionOption,
            ReviewResultTemplate reviewResultTemplate,
            String title,
            String summaryText,
            String feedbackTitle,
            String feedbackText,
            String guideTitle,
            String guideItem1,
            String guideItem2,
            String guideItem3
    ) {
        this.userId = userId;
        this.reviewDate = reviewDate;
        this.emotion = emotion;
        this.situationTag = situationTag;
        this.satisfactionOption = satisfactionOption;
        this.nextActionOption = nextActionOption;
        this.reviewResultTemplate = reviewResultTemplate;
        this.title = title;
        this.summaryText = summaryText;
        this.feedbackTitle = feedbackTitle;
        this.feedbackText = feedbackText;
        this.guideTitle = guideTitle;
        this.guideItem1 = guideItem1;
        this.guideItem2 = guideItem2;
        this.guideItem3 = guideItem3;
    }

    public static Review create(
            Long userId,
            LocalDate reviewDate,
            Emotion emotion,
            SituationTag situationTag,
            ReviewChoiceOption satisfactionOption,
            ReviewChoiceOption nextActionOption,
            ReviewResultTemplate reviewResultTemplate,
            String title,
            String summaryText,
            String feedbackTitle,
            String feedbackText,
            String guideTitle,
            String guideItem1,
            String guideItem2,
            String guideItem3
    ) {
        return new Review(
                userId,
                reviewDate,
                emotion,
                situationTag,
                satisfactionOption,
                nextActionOption,
                reviewResultTemplate,
                title,
                summaryText,
                feedbackTitle,
                feedbackText,
                guideTitle,
                guideItem1,
                guideItem2,
                guideItem3
        );
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}