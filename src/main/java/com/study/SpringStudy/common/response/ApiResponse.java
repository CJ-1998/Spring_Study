package com.study.SpringStudy.common.response;

import com.study.SpringStudy.common.exception.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

  private final String result; // "SUCCESS" or "ERROR"
  private final T data; // 성공 데이터
  private final String errorCode; // 에러 코드 (성공 시 null)
  private final String message; // 에러 메시지 (성공 시 null)

  // 1. 성공 응답 생성자
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>("SUCCESS", data, null, null);
  }

  // 2. 실패 응답 생성자 (데이터 없이 에러 내용만)
  public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
    return new ApiResponse<>("ERROR", null, errorCode.getCode(), errorCode.getMessage());
  }

  // 3. 실패 응답인데 데이터도 필요할 때 (예: 유효성 검사 실패 필드 목록)
  public static <T> ApiResponse<T> fail(ErrorCode errorCode, String detailMessage) {
    return new ApiResponse<>("ERROR", null, errorCode.getCode(), detailMessage);
  }
}
