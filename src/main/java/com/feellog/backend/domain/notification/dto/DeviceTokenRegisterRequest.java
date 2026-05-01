package com.feellog.backend.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceTokenRegisterRequest(
        @NotBlank String token,
        String deviceType
) {}