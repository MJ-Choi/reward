package com.product.reward.reward.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardEditRequest {

    @Min(1)
    private Long rid;
    private String rewardDate;  // 지급 대상 날짜(리워드를 지급하는 날짜)
    private String requestDate;  // 요청 시점(리워드 집계 기준일)
    private String rewardType;  // 리워드 종류
    private String state;   // 요청 상태

}