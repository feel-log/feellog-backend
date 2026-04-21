package com.feellog.backend.domain.user.service;

import com.feellog.backend.domain.user.dto.TokenResponse;
import com.feellog.backend.domain.user.entity.RefreshToken;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.repository.RefreshTokenRepository;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
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
    public TokenResponse issueTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        token -> token.rotate(newRefreshToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .token(newRefreshToken)
                                .expiresAt(expiresAt)
                                .build())
                );

        return new TokenResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }
}