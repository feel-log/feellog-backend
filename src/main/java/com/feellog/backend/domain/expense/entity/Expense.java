package com.feellog.backend.domain.expense.entity;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.paymentmethod.entity.PaymentMethod;
import com.feellog.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 결제 수단
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    // 금액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // 날짜
    @Column(nullable = false)
    private LocalDate expenseDate;

    // 시간 (optional)
    private LocalTime expenseTime;

    // 가맹점
    @Column(length = 150)
    private String merchantName;

    // 메모
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 삭제 여부
    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}