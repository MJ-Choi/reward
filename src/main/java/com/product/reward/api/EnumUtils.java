package com.product.reward.api;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumUtils {

    public static <T extends Enum<T> & EnumFinder> T fromCode(Class<T> enumClass, String code) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getCode().equals(code)) {
                return enumConstant;
            }
        }
        log.error("failed to find enum: {}", code);
        throw new ResponseException(ErrorCode.INPUT_ERROR);
    }
}

