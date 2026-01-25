package com.study.SpringStudy.common.exception.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
  String name();

  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}
