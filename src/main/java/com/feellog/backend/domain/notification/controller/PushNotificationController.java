package com.feellog.backend.domain.notification.controller;

import com.feellog.backend.domain.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/test")
    public ResponseEntity<Void> sendTestPush(@AuthenticationPrincipal Long userId) {
        boolean sent = pushNotificationService.sendTestPush(userId);
        return sent ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }
}