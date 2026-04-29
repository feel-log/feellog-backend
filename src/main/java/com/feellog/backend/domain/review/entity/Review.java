package com.feellog.backend.domain.review.entity;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.catalog.situationtag.entity.SituationTag;
import com.feellog.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_review_user_date", columnNames = {"user_id", "review_date"})
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
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
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Review create(
            User user,
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
        Review review = new Review();

        review.user = user;
        review.reviewDate = reviewDate;
        review.emotion = emotion;
        review.situationTag = situationTag;
        review.satisfactionOption = satisfactionOption;
        review.nextActionOption = nextActionOption;
        review.reviewResultTemplate = reviewResultTemplate;

        review.title = title;
        review.summaryText = summaryText;
        review.feedbackTitle = feedbackTitle;
        review.feedbackText = feedbackText;
        review.guideTitle = guideTitle;
        review.guideItem1 = guideItem1;
        review.guideItem2 = guideItem2;
        review.guideItem3 = guideItem3;

        LocalDateTime now = LocalDateTime.now();
        review.createdAt = now;
        review.updatedAt = now;

        return review;
    }
}