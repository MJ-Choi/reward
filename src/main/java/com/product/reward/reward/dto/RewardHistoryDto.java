package com.product.reward.reward.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RewardHistoryDto {

    private String comicName;   //작품명
    private String type;    //리워드 유형
    private String rewardDtm;   //리워드 시간
    private int rank;   // 순위
    private int point;  // 점수

}