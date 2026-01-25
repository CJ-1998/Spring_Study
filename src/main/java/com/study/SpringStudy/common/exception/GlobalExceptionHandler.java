package com.study.SpringStudy.common.exception;

import com.study.SpringStudy.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
    log.warn("ApiException: {}", e.getErrorCode().getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(ApiResponse.fail(e.getErrorCode()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("Unhandled Exception: ", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR));
  }
}
