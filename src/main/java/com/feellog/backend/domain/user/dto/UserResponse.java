package com.feellog.backend.domain.user.dto;

import com.feellog.backend.domain.user.entity.User;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        LocalDate birthDate,
        String gender,
        String provider
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getBirthDate(),
                user.getGender(),
                user.getProvider().name()
        );
    }
}