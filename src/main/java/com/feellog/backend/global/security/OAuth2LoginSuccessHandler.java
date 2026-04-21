package com.feellog.backend.global.security;

import com.feellog.backend.domain.user.entity.RefreshToken;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.repository.RefreshTokenRepository;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.jwt.JwtProvider;
import com.feellog.backend.global.oauth2.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getUserId();

        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        User user = userRepository.findById(userId).orElseThrow();
        saveOrRotateRefreshToken(user, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\"}",
                accessToken, refreshToken
        ));
    }

    private void saveOrRotateRefreshToken(User user, String newToken) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        token -> token.rotate(newToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .token(newToken)
                                .expiresAt(expiresAt)
                                .build())
                );
    }
}