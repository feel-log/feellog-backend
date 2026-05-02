package com.feellog.backend.global.oauth2.client;

import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleAuthClient {

    private final RestClient restClient = RestClient.create();

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
