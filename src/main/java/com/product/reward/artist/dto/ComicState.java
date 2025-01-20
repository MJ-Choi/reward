package com.product.reward.artist.dto;

import com.product.reward.api.EnumFinder;
import com.product.reward.api.EnumUtils;
import lombok.Getter;

@Getter
public enum ComicState implements EnumFinder {
    RESERVED("01", "오픈 예약"),
    OPEN("02", "연재중"),
    SLEEP("03", "휴재"),
    COMPLETE("04", "완결"),
    CLOSE("05", "서비스 종료"),
    ;

    private String code;
    private String desc;

    ComicState(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ComicState fromCode(String code) {
        return EnumUtils.fromCode(ComicState.class, code);
    }
}
