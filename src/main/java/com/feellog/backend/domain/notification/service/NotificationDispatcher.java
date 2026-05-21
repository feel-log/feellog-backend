package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.notification.entity.Notification;
import com.feellog.backend.domain.notification.entity.NotificationSettings;
import com.feellog.backend.domain.notification.entity.NotificationType;
import com.feellog.backend.domain.notification.repository.NotificationRepository;
import com.feellog.backend.domain.notification.repository.NotificationSettingsRepository;
import com.feellog.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationRepository notificationRepository;

    // settings row가 없으면 OFF로 간주 (안전한 기본값)
    @Transactional(readOnly = true)
    public boolean isPushEnabled(User user) {
        return notificationSettingsRepository.findByUser(user)
                .map(NotificationSettings::isPushEnabled)
                .orElse(false);
    }

    // 유저 단위 독립 트랜잭션. 한 유저 실패가 다른 유저 루프에 전파되지 않도록 REQUIRES_NEW.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveIfEnabled(User user, NotificationType type, String title, String body) {
        boolean enabled = notificationSettingsRepository.findByUser(user)
                .map(NotificationSettings::isPushEnabled)
                .orElse(false);
        if (!enabled) {
            return false;
        }
        notificationRepository.save(Notification.of(user, type, title, body));
        return true;
    }
}