package com.feellog.backend.domain.notification.controller;

import com.feellog.backend.domain.notification.dto.DeviceTokenRegisterRequest;
import com.feellog.backend.domain.notification.service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping
    public ResponseEntity<Void> registerToken(@AuthenticationPrincipal Long userId,
                                               @Valid @RequestBody DeviceTokenRegisterRequest request) {
        deviceTokenService.registerToken(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteToken(@AuthenticationPrincipal Long userId,
                                             @RequestParam String token) {
        deviceTokenService.deleteToken(userId, token);
        return ResponseEntity.noContent().build();
    }
}