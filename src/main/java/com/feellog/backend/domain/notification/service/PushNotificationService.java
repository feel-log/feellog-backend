package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.expense.repository.ExpenseRepository;
import com.feellog.backend.domain.notification.entity.DailyReviewMessage;
import com.feellog.backend.domain.notification.entity.DeviceToken;
import com.feellog.backend.domain.notification.entity.ExpenseReminderMessage;
import com.feellog.backend.domain.notification.entity.NotificationType;
import com.feellog.backend.domain.notification.repository.DeviceTokenRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final FcmService fcmService;
    private final NotificationDispatcher notificationDispatcher;

    // 테스트 푸시는 알림함에 저장하지 않음 (정책상 디바이스 토큰 검증용)
    public boolean sendTestPush(Long userId) {
        User user = findActiveUser(userId);
        if (!notificationDispatcher.isPushEnabled(user)) {
            return false;
        }
        return sendFcm(
                user,
                "FeelLog 테스트 알림",
                "푸시 알림이 정상적으로 수신되었습니다."
        );
    }

    public void sendExpenseReminder() {
        List<User> activeUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);
        LocalDate today = LocalDate.now();

        for (User user : activeUsers) {
            boolean hasExpenseToday = !expenseRepository
                    .findByUserAndExpenseDateAndIsDeletedFalse(user, today)
                    .isEmpty();
            if (hasExpenseToday) {
                continue;
            }

            ExpenseReminderMessage message = ExpenseReminderMessage.getRandom();
            if (!notificationDispatcher.saveIfEnabled(user, NotificationType.EXPENSE_REMINDER, message.getBody())) {
                continue;
            }
            sendFcm(user, message.getTitle(), message.getBody());
        }
    }

    public void sendDailyReview() {
        List<User> activeUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);

        for (User user : activeUsers) {
            DailyReviewMessage message = DailyReviewMessage.getRandom();
            if (!notificationDispatcher.saveIfEnabled(user, NotificationType.DAILY_REVIEW, message.getBody())) {
                continue;
            }
            sendFcm(user, message.getTitle(), message.getBody());
        }
    }

    // FCM은 외부 IO이므로 트랜잭션 외부에서 호출. 토큰 0개면 알림함에는 이미 저장됐어도 false 반환.
    private boolean sendFcm(User user, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
        if (tokens.isEmpty()) {
            return false;
        }
        tokens.forEach(dt -> fcmService.sendMessage(dt.getToken(), title, body));
        return true;
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}