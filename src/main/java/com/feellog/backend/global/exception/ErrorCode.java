package com.feellog.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴한 유저입니다."),

    // 토큰
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 소셜 로그인
    SOCIAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패했습니다."),

    // 회고
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 날짜의 회고가 존재하지 않습니다."),
    EMOTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 감정입니다."),
    SITUATION_TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 소비 상황입니다."),
    REVIEW_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회고 선택지입니다."),
    INVALID_YEAR_MONTH(HttpStatus.BAD_REQUEST, "유효하지 않은 연도 또는 월입니다."),

    // 자산
    ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 자산 기록입니다."),
    ASSET_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 자산 카테고리입니다."),
    INVALID_ASSET_AMOUNT(HttpStatus.BAD_REQUEST, "자산 금액은 0원 이상이어야 합니다."),
    INVALID_ASSET_SORT_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 자산 정렬 조건입니다."),

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다."),

    // 알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다.");

    private final HttpStatus status;
    private final String message;
}