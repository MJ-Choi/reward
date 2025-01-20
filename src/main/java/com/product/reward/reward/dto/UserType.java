package com.product.reward.reward.dto;

import com.product.reward.api.EnumFinder;
import com.product.reward.api.EnumUtils;
import lombok.Getter;

@Getter
public enum UserType implements EnumFinder {
    ARTIST("a", "작가"),
    USER("m", "소비자"),
    ;

    private String code;
    private String desc;

    UserType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserType fromCode(String code) {
        return EnumUtils.fromCode(UserType.class, code);
    }
}
