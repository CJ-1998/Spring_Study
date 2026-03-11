package com.study.SpringStudy.common.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_0001", "요청한 user를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_0002", "비밀번호가 맞지 않습니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "USER_0003", "이미 존재하는 email 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
