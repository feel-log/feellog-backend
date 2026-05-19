package com.feellog.backend.domain.notification.controller;

import com.feellog.backend.domain.notification.dto.NotificationSettingsResponse;
import com.feellog.backend.domain.notification.dto.NotificationSettingsUpdateRequest;
import com.feellog.backend.domain.notification.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @GetMapping
    public ResponseEntity<NotificationSettingsResponse> getMySettings(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(notificationSettingsService.getMySettings(userId));
    }

    @PatchMapping
    public ResponseEntity<Void> updateMySettings(
            @AuthenticationPrincipal Long userId,
            @RequestBody NotificationSettingsUpdateRequest request
    ) {
        notificationSettingsService.updateMySettings(userId, request.pushEnabled());
        return ResponseEntity.noContent().build();
    }
}