package com.feellog.backend.domain.review.entity;

import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
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
            ReviewChoiceOption nextActionOption
    ) {
        Review review = new Review();
        review.user = user;
        review.reviewDate = reviewDate;
        review.emotion = emotion;
        review.situationTag = situationTag;
        review.satisfactionOption = satisfactionOption;
        review.nextActionOption = nextActionOption;
        review.createdAt = LocalDateTime.now();
        review.updatedAt = LocalDateTime.now();
        return review;
    }

    public void update(
            Emotion emotion,
            SituationTag situationTag,
            ReviewChoiceOption satisfactionOption,
            ReviewChoiceOption nextActionOption
    ) {
        this.emotion = emotion;
        this.situationTag = situationTag;
        this.satisfactionOption = satisfactionOption;
        this.nextActionOption = nextActionOption;
        this.updatedAt = LocalDateTime.now();
    }
}