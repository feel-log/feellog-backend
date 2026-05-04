package com.feellog.backend.domain.expense.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.feellog.backend.domain.situationtag.entity.SituationTag;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(
        name = "expense_situation_tag",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expense_situation",
                        columnNames = {"expense_id", "situation_tag_id"}
                )
        }
)
public class ExpenseSituationTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_situation_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situation_tag_id", nullable = false)
    private SituationTag situationTag;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}