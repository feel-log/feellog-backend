package com.feellog.backend.domain.user.service;

import com.feellog.backend.domain.user.dto.TokenResponse;
import com.feellog.backend.domain.user.entity.Provider;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.oauth2.client.GoogleAuthClient;
import com.feellog.backend.global.oauth2.client.GoogleUserInfo;
import com.feellog.backend.global.oauth2.client.KakaoAuthClient;
import com.feellog.backend.global.oauth2.client.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final GoogleAuthClient googleAuthClient;
    private final UserRepository userRepository;
    private final AuthService authService;

    public TokenResponse kakaoLogin(String accessToken) {
        KakaoUserInfo userInfo = kakaoAuthClient.getUserInfo(accessToken);

        String providerUserId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
        String nickname = (userInfo.kakaoAccount() != null && userInfo.kakaoAccount().profile() != null)
                ? userInfo.kakaoAccount().profile().nickname()
                : "카카오 사용자";

        return findOrCreateAndIssueTokens(Provider.KAKAO, providerUserId, email, nickname);
    }

    public TokenResponse googleLogin(String accessToken) {
        GoogleUserInfo userInfo = googleAuthClient.getUserInfo(accessToken);

        return findOrCreateAndIssueTokens(Provider.GOOGLE, userInfo.id(), userInfo.email(), userInfo.name());
    }

    public TokenResponse guestLogin() {
        String providerUserId = UUID.randomUUID().toString();
        String suffix = providerUserId.replace("-", "").substring(0, 4).toUpperCase();
        String nickname = "guest_" + suffix;

        User user = userRepository.save(User.builder()
                .provider(Provider.GUEST)
                .providerUserId(providerUserId)
                .email(null)
                .nickname(nickname)
                .build());

        return authService.issueTokens(user.getId());
    }

    private TokenResponse findOrCreateAndIssueTokens(Provider provider, String providerUserId,
                                                      String email, String nickname) {
        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .email(email)
                        .nickname(nickname != null ? nickname : "사용자")
                        .build()));

        user.updateLastLoginAt();
        return authService.issueTokens(user.getId());
    }
}