package com.product.reward.api.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * API 응답 시, 사용자 에러 반환
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(ResponseException.class)
  public ResponseEntity<Map<String, String>> handleApiException(ResponseException ex) {
    return ResponseEntity.ok(ex.getResult());
  }
}
