package com.feellog.backend.domain.expense.entity;

import com.feellog.backend.domain.catalog.category.entity.Category;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "expense_time")
    private LocalTime expenseTime;

    @Column(name = "merchant_name", length = 150)
    private String merchantName;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "expense", fetch = FetchType.LAZY)
    private Set<ExpenseEmotion> expenseEmotions = new HashSet<>();

    @OneToMany(mappedBy = "expense", fetch = FetchType.LAZY)
    private Set<ExpenseSituationTag> expenseSituationTags = new HashSet<>();
}