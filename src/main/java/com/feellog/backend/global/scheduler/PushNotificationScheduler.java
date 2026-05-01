package com.feellog.backend.global.scheduler;

import com.feellog.backend.domain.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationScheduler {

    private final PushNotificationService pushNotificationService;

    @Scheduled(cron = "0 0 19 * * *", zone = "Asia/Seoul")
    public void sendExpenseReminder() {
        log.info("지출 기록 유도 푸시 발송 시작");
        pushNotificationService.sendExpenseReminder();
        log.info("지출 기록 유도 푸시 발송 완료");
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void sendDailyReview() {
        log.info("일일 회고 유도 푸시 발송 시작");
        pushNotificationService.sendDailyReview();
        log.info("일일 회고 유도 푸시 발송 완료");
    }
}