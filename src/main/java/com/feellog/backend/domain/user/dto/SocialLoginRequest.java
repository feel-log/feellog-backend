package com.feellog.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(@NotBlank String code) {}