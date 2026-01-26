package com.study.SpringStudy.common.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_0001", "잘못된 입력 값입니다."),
  INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_0002", "잘못된 타입입니다."),
  NOT_EXIST_API(HttpStatus.BAD_REQUEST, "COMMON_0003", "요청 주소가 올바르지 않습니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_0004", "사용할 수 없는 메서드입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_0005", "서버 에러입니다."),
  INVALID_JSON(HttpStatus.BAD_REQUEST, "COMMON_0006", "JSON 파싱 오류입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_0007", "요청한 리소스를 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
