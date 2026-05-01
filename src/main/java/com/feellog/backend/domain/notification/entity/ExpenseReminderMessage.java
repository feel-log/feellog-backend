package com.feellog.backend.domain.notification.entity;

import java.util.concurrent.ThreadLocalRandom;

public enum ExpenseReminderMessage {

    MSG_1("오늘 쓴 돈, 아직 기록하지 않았어요"),
    MSG_2("오늘의 소비를 가볍게 기록해볼까요?"),
    MSG_3("방금 쓴 지출, 잊기 전에 기록해보세요"),
    MSG_4("오늘 소비한 내역이 있다면 기록해보세요"),
    MSG_5("오늘의 지출 기록을 남겨주세요");

    private static final String TITLE = "FeelLog";
    private final String body;

    ExpenseReminderMessage(String body) {
        this.body = body;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getBody() {
        return body;
    }

    public static ExpenseReminderMessage getRandom() {
        ExpenseReminderMessage[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
