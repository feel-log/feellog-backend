package com.feellog.backend.domain.notification.dto;

import com.feellog.backend.domain.notification.entity.Notification;
import com.feellog.backend.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        String title,
        String body,
        boolean isRead,
        OffsetDateTime createdAt,
        OffsetDateTime readAt
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                toKstOffsetDateTime(notification.getCreatedAt()),
                toKstOffsetDateTime(notification.getReadAt())
        );
    }

    private static OffsetDateTime toKstOffsetDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(KST).toOffsetDateTime();
    }
}
