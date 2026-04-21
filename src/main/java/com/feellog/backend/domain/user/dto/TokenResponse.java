package com.feellog.backend.domain.user.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}