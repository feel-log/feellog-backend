package com.feellog.backend.domain.notification.entity;

import java.util.concurrent.ThreadLocalRandom;

public enum ExpenseReminderMessage {

    MSG_1("오늘 쓴 돈, 아직 기록하지 않았어요",
            "지금 간단히 남겨두면 이번 달 소비 흐름을 더 정확히 볼 수 있어요."),
    MSG_2("오늘의 소비를 가볍게 기록해볼까요?",
            "금액과 감정만 남겨도 나의 소비 패턴을 알 수 있어요."),
    MSG_3("방금 쓴 지출, 잊기 전에 기록해보세요",
            "작은 기록이 쌓이면 나의 소비 습관이 보여요."),
    MSG_4("오늘 소비한 내역이 있다면 기록해보세요",
            "나중에 돌아볼 수 있도록 지금 짧게 남겨둘 수 있어요."),
    MSG_5("오늘의 지출 기록을 남겨주세요",
            "소비와 감정을 함께 기록하면 더 의미 있는 리포트를 만들 수 있어요.");

    private final String title;
    private final String body;

    ExpenseReminderMessage(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public static ExpenseReminderMessage getRandom() {
        ExpenseReminderMessage[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}