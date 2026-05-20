package com.feellog.backend.domain.notification.entity;

import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "body", nullable = false, length = 255)
    private String body;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Notification(User user, NotificationType type, String body) {
        this.user = user;
        this.type = type;
        this.body = body;
    }

    public static Notification of(User user, NotificationType type, String body) {
        return Notification.builder()
                .user(user)
                .type(type)
                .body(body)
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
