package com.product.reward.reward.dto;

import com.product.reward.api.EnumFinder;
import com.product.reward.api.EnumUtils;
import lombok.Getter;

@Getter
public enum RewardType implements EnumFinder {
    DAILY("D", "일간"),
    WEEKLY("W", "주간"),
    MONTHLY("M", "월간"),
    YEARLY("Y", "연간"),
    ;

    private String code;
    private String desc;

    RewardType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static RewardType fromCode(String code) {
        return EnumUtils.fromCode(RewardType.class, code);
    }
}
