package com.feellog.backend.domain.notification.entity;

import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "device_tokens")
public class DeviceToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Builder
    public DeviceToken(User user, String token, String deviceType) {
        this.user = user;
        this.token = token;
        this.deviceType = deviceType;
        this.lastUsedAt = LocalDateTime.now();
    }

    public void updateInfo(User user, String deviceType) {
        this.user = user;
        this.deviceType = deviceType;
        this.lastUsedAt = LocalDateTime.now();
    }
}