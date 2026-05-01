package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.notification.dto.DeviceTokenRegisterRequest;
import com.feellog.backend.domain.notification.entity.DeviceToken;
import com.feellog.backend.domain.notification.repository.DeviceTokenRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerToken(Long userId, DeviceTokenRegisterRequest request) {
        User user = findActiveUser(userId);

        deviceTokenRepository.findByToken(request.token())
                .ifPresentOrElse(
                        existing -> existing.updateInfo(user, request.deviceType()),
                        () -> deviceTokenRepository.save(
                                DeviceToken.builder()
                                        .user(user)
                                        .token(request.token())
                                        .deviceType(request.deviceType())
                                        .build()
                        )
                );
    }

    @Transactional
    public void deleteToken(Long userId, String token) {
        User user = findActiveUser(userId);
        deviceTokenRepository.deleteByUserAndToken(user, token);
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}