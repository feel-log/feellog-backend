package com.feellog.backend.global.security;

import com.feellog.backend.domain.user.dto.TokenResponse;
import com.feellog.backend.domain.user.service.AuthService;
import com.feellog.backend.global.oauth2.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        TokenResponse tokens = authService.issueTokens(oAuth2User.getUserId());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\"}",
                tokens.accessToken(), tokens.refreshToken()
        ));
    }
}