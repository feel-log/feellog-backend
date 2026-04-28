package com.feellog.backend.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_choice_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewChoiceOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_choice_option_id")
    private Long id;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType;

    @Column(name = "option_text", nullable = false, length = 255)
    private String optionText;

    @Column(name = "option_value", nullable = false, length = 100)
    private String optionValue;

    @Column
    private Integer score;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}