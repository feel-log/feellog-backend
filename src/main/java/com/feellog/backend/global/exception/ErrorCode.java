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

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다.");

    private final HttpStatus status;
    private final String message;
}