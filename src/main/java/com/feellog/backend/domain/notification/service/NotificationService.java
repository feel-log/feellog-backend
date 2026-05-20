package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.notification.dto.NotificationResponse;
import com.feellog.backend.domain.notification.entity.Notification;
import com.feellog.backend.domain.notification.repository.NotificationRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        User user = findActiveUser(userId);
        return notificationRepository.findByUserAndIsDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        User user = findActiveUser(userId);
        notificationRepository.markAllAsReadByUser(user);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        User user = findActiveUser(userId);
        Notification notification = notificationRepository
                .findByNotificationIdAndUserAndIsDeletedFalse(notificationId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.isRead()) {
            notification.markAsRead();
        }
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        User user = findActiveUser(userId);
        Notification notification = notificationRepository
                .findByNotificationIdAndUserAndIsDeletedFalse(notificationId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.softDelete();
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        User user = findActiveUser(userId);
        notificationRepository.softDeleteAllByUser(user);
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}