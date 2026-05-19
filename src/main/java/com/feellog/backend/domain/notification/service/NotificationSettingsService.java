package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.notification.dto.NotificationSettingsResponse;
import com.feellog.backend.domain.notification.entity.NotificationSettings;
import com.feellog.backend.domain.notification.repository.NotificationSettingsRepository;
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
public class NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;

    // settings row가 없으면 OFF로 응답 (NotificationDispatcher와 동일한 안전 기본값)
    @Transactional(readOnly = true)
    public NotificationSettingsResponse getMySettings(Long userId) {
        User user = findActiveUser(userId);
        boolean pushEnabled = notificationSettingsRepository.findByUser(user)
                .map(NotificationSettings::isPushEnabled)
                .orElse(false);
        return new NotificationSettingsResponse(pushEnabled);
    }

    // row 없으면 새로 생성하면서 값 반영 (백필 누락 등 방어적 처리)
    @Transactional
    public void updateMySettings(Long userId, boolean pushEnabled) {
        User user = findActiveUser(userId);
        notificationSettingsRepository.findByUser(user)
                .ifPresentOrElse(
                        settings -> settings.updatePushEnabled(pushEnabled),
                        () -> notificationSettingsRepository.save(
                                NotificationSettings.builder()
                                        .user(user)
                                        .pushEnabled(pushEnabled)
                                        .build()
                        )
                );
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}