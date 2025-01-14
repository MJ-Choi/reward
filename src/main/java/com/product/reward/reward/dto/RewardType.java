package com.product.reward.reward.dto;

public enum RewardType {
    DAILY("01", "일간"),
    WEEKLY("02", "주간"),
    MONTHLY("02", "월간"),
    YEARLY("02", "연간"),
    ;

    private String code;
    private String desc;

    RewardType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
