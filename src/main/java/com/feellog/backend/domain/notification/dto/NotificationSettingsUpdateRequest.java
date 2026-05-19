package com.feellog.backend.domain.notification.dto;

public record NotificationSettingsUpdateRequest(
        boolean pushEnabled
) {
}