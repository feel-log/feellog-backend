package com.feellog.backend.domain.expense.entity;

import com.feellog.backend.domain.emotion.entity.Emotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "expense_emotion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expense_emotion",
                        columnNames = {"expense_id", "emotion_id"}
                )
        }
)
public class ExpenseEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_emotion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}