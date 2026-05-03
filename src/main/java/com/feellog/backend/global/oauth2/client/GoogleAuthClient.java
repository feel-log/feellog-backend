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
public class GoogleAuthClient {

    private final RestClient restClient = RestClient.create();
    private final String expectedClientId;

    public GoogleAuthClient(@Value("${oauth.google.client-id}") String expectedClientId) {
        this.expectedClientId = expectedClientId;
    }

    // 프론트가 GIS SDK로 받은 ID Token(response.credential)을 검증하고 사용자 정보 추출
    public GoogleUserInfo getUserInfo(String idToken) {
        GoogleTokenInfo tokenInfo = fetchAndValidateTokenInfo(idToken);
        return new GoogleUserInfo(
                tokenInfo.sub(),
                tokenInfo.email(),
                tokenInfo.name()
        );
    }

    // ID Token을 Google tokeninfo 엔드포인트로 검증 + audience(aud) 일치 확인
    private GoogleTokenInfo fetchAndValidateTokenInfo(String idToken) {
        log.info("[Google] id_token 검증 시작");
        try {
            GoogleTokenInfo tokenInfo = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                        log.error("[Google] id_token 검증 실패 — status={}, body={}",
                                res.getStatusCode(), new String(res.getBody().readAllBytes()));
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .body(GoogleTokenInfo.class);

            if (tokenInfo == null || tokenInfo.aud() == null) {
                log.error("[Google] id_token 응답이 null 또는 aud 누락");
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            if (!expectedClientId.equals(tokenInfo.aud())) {
                log.error("[Google] id_token aud 불일치 — expected={}, actual={}",
                        expectedClientId, tokenInfo.aud());
                throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            log.info("[Google] id_token 검증 성공 (aud: {})", tokenInfo.aud());
            return tokenInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("[Google] id_token 검증 중 네트워크 오류 — {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }

    private record GoogleTokenInfo(
            String aud,
            String sub,
            String email,
            String name
    ) {}
}