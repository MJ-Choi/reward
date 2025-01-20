package com.product.reward.reward.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RewardDto {

    private Long rid;   // 리워드ID
    private RewardType type;    // 리워드 종류
    private RewardState state;  // 리워드 상태
    private LocalDateTime rewardDtm;   // 리워드 시간(지급 대상 날짜)
    private LocalDateTime collectDtm;  // 요청 시점(집계 기준일)
    private List<RewardComicDto> top10Comics = null; // {cid: value}

    public RewardDto(Long rid, String type, String state, LocalDateTime rewardDtm, LocalDateTime collectDtm) {
        this.rid = rid;
        this.type = RewardType.fromCode(type);
        this.state = RewardState.fromCode(state);
        this.rewardDtm = rewardDtm;
        this.collectDtm = collectDtm;
    }
}
