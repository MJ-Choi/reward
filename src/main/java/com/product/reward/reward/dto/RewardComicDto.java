package com.product.reward.reward.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RewardComicDto {

    private Long cid;
    private Long aid;
    private int score;
    private int rank;
    private RewardType type;
}
