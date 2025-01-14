package com.product.reward.api.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
  SUCCESS(200, "정상응답"),
  CHECKED(400,"이미 등록한 브랜드입니다."),
  INPUT_ERROR(400, "잘못된 입력값입니다."),
  NO_DATA(404, "요청한 데이터가 없습니다.");

  private int code;
  private String msg;

  ErrorCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
