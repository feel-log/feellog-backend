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
        name = "review_guide_phrase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_guide_phrase",
                        columnNames = {"next_action_group_code"}
                )
        }
)
public class ReviewGuidePhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_guide_phrase_id")
    private Long id;

    @Column(name = "next_action_group_code", nullable = false, length = 50)
    private String nextActionGroupCode;

    @Column(name = "guide_title", nullable = false, length = 255)
    private String guideTitle;

    @Column(name = "guide_item_1", nullable = false, length = 255)
    private String guideItem1;

    @Column(name = "guide_item_2", nullable = false, length = 255)
    private String guideItem2;

    @Column(name = "guide_item_3", nullable = false, length = 255)
    private String guideItem3;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}