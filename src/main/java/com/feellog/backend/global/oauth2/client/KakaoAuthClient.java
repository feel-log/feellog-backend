package com.feellog.backend.global.oauth2.client;

import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class KakaoAuthClient {

    private final RestClient restClient = RestClient.create();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        try {
            KakaoTokenResponse response = restClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                        log.error("[Kakao] 토큰 요청 실패 — status={}, body={}", res.getStatusCode(), new String(res.getBody().readAllBytes()));
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .body(KakaoTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                log.error("[Kakao] 토큰 응답이 null");
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            log.debug("[Kakao] 액세스토큰 발급 성공");
            return response.accessToken();
        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("[Kakao] 토큰 요청 중 네트워크 오류 — {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserInfo userInfo = restClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                        log.error("[Kakao] 유저정보 요청 실패 — status={}, body={}", res.getStatusCode(), new String(res.getBody().readAllBytes()));
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .body(KakaoUserInfo.class);

            if (userInfo == null) {
                log.error("[Kakao] 유저정보 응답이 null");
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            log.debug("[Kakao] 유저정보 조회 성공 — id={}", userInfo.id());
            return userInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("[Kakao] 유저정보 요청 중 네트워크 오류 — {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
