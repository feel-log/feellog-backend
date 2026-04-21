package com.feellog.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(
        @NotBlank @Size(max = 100) String nickname,
        LocalDate birthDate,
        @Size(max = 20) String gender
) {
}