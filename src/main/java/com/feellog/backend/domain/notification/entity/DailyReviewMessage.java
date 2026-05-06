package com.feellog.backend.domain.notification.entity;

import java.util.concurrent.ThreadLocalRandom;

public enum DailyReviewMessage {

    MSG_1("오늘의 소비를 돌아볼 시간이에요",
            "오늘 어떤 마음으로 소비했는지 짧게 회고해보세요."),
    MSG_2("오늘 하루, 어떤 소비가 기억에 남나요?",
            "지출과 감정을 함께 돌아보며 나의 패턴을 확인해보세요."),
    MSG_3("오늘의 소비 감정을 정리해볼까요?",
            "하루를 마무리하며 소비 뒤에 남은 마음을 기록해보세요."),
    MSG_4("잠들기 전, 오늘의 소비를 회고해보세요",
            "기록이 쌓일수록 나에게 맞는 소비 패턴이 보여요."),
    MSG_5("오늘 소비는 나에게 어떤 의미였을까요?",
            "짧은 회고로 내 소비 습관을 더 잘 이해할 수 있어요.");

    private final String title;
    private final String body;

    DailyReviewMessage(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public static DailyReviewMessage getRandom() {
        DailyReviewMessage[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}