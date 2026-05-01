package com.feellog.backend.domain.notification.service;

import com.feellog.backend.domain.expense.repository.ExpenseRepository;
import com.feellog.backend.domain.notification.entity.DailyReviewMessage;
import com.feellog.backend.domain.notification.entity.DeviceToken;
import com.feellog.backend.domain.notification.entity.ExpenseReminderMessage;
import com.feellog.backend.domain.notification.repository.DeviceTokenRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final FcmService fcmService;

    @Transactional(readOnly = true)
    public boolean sendTestPush(Long userId) {
        User user = findActiveUser(userId);
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);

        if (tokens.isEmpty()) {
            return false;
        }

        tokens.forEach(dt -> fcmService.sendMessage(
                dt.getToken(),
                "FeelLog 테스트 알림",
                "푸시 알림이 정상적으로 수신되었습니다."
        ));
        return true;
    }

    @Transactional(readOnly = true)
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

            List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
            if (tokens.isEmpty()) {
                continue;
            }

            ExpenseReminderMessage message = ExpenseReminderMessage.getRandom();
            tokens.forEach(dt -> fcmService.sendMessage(dt.getToken(), message.getTitle(), message.getBody()));
        }
    }

    @Transactional(readOnly = true)
    public void sendDailyReview() {
        List<User> activeUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);

        for (User user : activeUsers) {
            List<DeviceToken> tokens = deviceTokenRepository.findByUser(user);
            if (tokens.isEmpty()) {
                continue;
            }

            DailyReviewMessage message = DailyReviewMessage.getRandom();
            tokens.forEach(dt -> fcmService.sendMessage(dt.getToken(), message.getTitle(), message.getBody()));
        }
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}