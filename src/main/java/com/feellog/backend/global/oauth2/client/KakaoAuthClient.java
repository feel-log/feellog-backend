package com.feellog.backend.global.oauth2.client;

import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class KakaoAuthClient {

    private final RestClient restClient = RestClient.create();
    private final Long expectedAppId;

    public KakaoAuthClient(@Value("${oauth.kakao.app-id}") Long expectedAppId) {
        this.expectedAppId = expectedAppId;
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        validateAudience(accessToken);

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

    // 프론트가 보낸 access_token이 우리 카카오 앱에서 발급된 것인지 검증 (audience 검증)
    private void validateAudience(String accessToken) {
        log.info("[Kakao] access_token 검증 시작");
        try {
            KakaoTokenInfo tokenInfo = restClient.get()
                    .uri("https://kapi.kakao.com/v1/user/access_token_info")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                        log.error("[Kakao] access_token 검증 실패 — status={}, body={}",
                                res.getStatusCode(), new String(res.getBody().readAllBytes()));
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .body(KakaoTokenInfo.class);

            if (tokenInfo == null || tokenInfo.app_id() == null) {
                log.error("[Kakao] access_token 응답이 null 또는 app_id 누락");
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            if (!expectedAppId.equals(tokenInfo.app_id())) {
                log.error("[Kakao] access_token app_id 불일치 — expected={}, actual={}",
                        expectedAppId, tokenInfo.app_id());
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            log.info("[Kakao] access_token 검증 성공 (app_id: {})", tokenInfo.app_id());
        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("[Kakao] access_token 검증 중 네트워크 오류 — {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }

    private record KakaoTokenInfo(
            Long id,
            Integer expires_in,
            Long app_id
    ) {}
}
