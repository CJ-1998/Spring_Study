package com.study.SpringStudy.common.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_0001", "유효하지 않은 access 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_0001", "유효하지 않은 refresh 토큰입니다."),
    TIMEOUT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_0003", "만료된 refresh 토큰입니다."),
    TIMEOUT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_0004", "만료된 refresh 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}