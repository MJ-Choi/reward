package com.product.reward.reward.dto;

import com.product.reward.api.EnumFinder;
import com.product.reward.api.EnumUtils;
import lombok.Getter;

@Getter
public enum RewardState implements EnumFinder {
    CREATED("01", "요청 생성"),
    COMPLETE("02", "처리 완료"),
    FAIL("10", "실패"),
    CANCEL("11", "취소"),
    ;
    private String code;
    private String desc;

    RewardState(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RewardState fromCode(String code) {
        return EnumUtils.fromCode(RewardState.class, code);
    }
}
