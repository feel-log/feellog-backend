package com.feellog.backend.global.oauth2.client;

import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class GoogleAuthClient {

    private final RestClient restClient = RestClient.create();

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        GoogleTokenResponse response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        (req, res) -> { throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED); })
                .body(GoogleTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
        return response.accessToken();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        GoogleUserInfo userInfo = restClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        (req, res) -> { throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED); })
                .body(GoogleUserInfo.class);

        if (userInfo == null) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
        return userInfo;
    }
}