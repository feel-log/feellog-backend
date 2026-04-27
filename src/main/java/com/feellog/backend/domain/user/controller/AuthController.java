package com.feellog.backend.domain.user.controller;

import com.feellog.backend.domain.user.dto.RefreshRequest;
import com.feellog.backend.domain.user.dto.SocialLoginRequest;
import com.feellog.backend.domain.user.dto.TokenResponse;
import com.feellog.backend.domain.user.service.AuthService;
import com.feellog.backend.domain.user.service.SocialAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SocialAuthService socialAuthService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(socialAuthService.kakaoLogin(request.code()));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(socialAuthService.googleLogin(request.code()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}