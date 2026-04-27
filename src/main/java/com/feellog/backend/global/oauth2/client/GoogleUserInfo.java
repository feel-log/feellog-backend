package com.feellog.backend.global.oauth2.client;

public record GoogleUserInfo(
        String id,
        String email,
        String name
) {}
