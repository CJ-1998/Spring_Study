package com.study.SpringStudy.common.exception;

import com.study.SpringStudy.common.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
  private final ErrorCode errorCode;
}

