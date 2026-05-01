package com.feellog.backend.domain.notification.entity;

import java.util.concurrent.ThreadLocalRandom;

public enum DailyReviewMessage {

    MSG_1("오늘의 소비를 돌아볼 시간이에요"),
    MSG_2("오늘 하루, 어떤 소비가 기억에 남나요?"),
    MSG_3("오늘의 소비 감정을 정리해볼까요?"),
    MSG_4("잠들기 전, 오늘의 소비를 회고해보세요"),
    MSG_5("오늘 소비는 나에게 어떤 의미였을까요?");

    private static final String TITLE = "FeelLog";
    private final String body;

    DailyReviewMessage(String body) {
        this.body = body;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getBody() {
        return body;
    }

    public static DailyReviewMessage getRandom() {
        DailyReviewMessage[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}