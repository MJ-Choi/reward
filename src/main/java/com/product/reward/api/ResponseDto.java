package com.product.reward.api;

import com.product.reward.api.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 응답 형태
 * @param <T>
 */
@Setter
@Getter
@ToString
public class ResponseDto<T> {

  private int code;
  private T contents;

  public ResponseDto(ErrorCode code, T contents) {
    this.code = code.getCode();
    this.contents = contents;
  }

  public ResponseDto(T contents) {
    this.code = ErrorCode.SUCCESS.getCode();
    this.contents = contents;
  }
}
