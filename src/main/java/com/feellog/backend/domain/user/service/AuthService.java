package com.feellog.backend.domain.user.service;

import com.feellog.backend.domain.user.dto.TokenResponse;
import com.feellog.backend.domain.user.entity.RefreshToken;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.repository.RefreshTokenRepository;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다."));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        Long userId = refreshToken.getUser().getId();
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        LocalDateTime newExpiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpiration() / 1000);
        refreshToken.rotate(newRefreshToken, newExpiresAt);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        refreshTokenRepository.deleteByUser(user);
    }
}