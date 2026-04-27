package com.feellog.backend.global.oauth2.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            KakaoProfile profile
    ) {
        public record KakaoProfile(String nickname) {}
    }
}
