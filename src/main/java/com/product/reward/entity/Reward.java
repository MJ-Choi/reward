package com.product.reward.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reward")
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rid;  // 리워드 ID

    @Column(name = "reward_type", length = 4)
    private String rewardType;  // 리워드 종류(일간, 주간, 월간, 연간)

    @Column(name = "state", length = 4)
    private String state;  // 리워드 상태

    @Column(name = "reward_dtm", nullable = false, updatable = false)
    private LocalDateTime rewardDtm;  // 리워드 시간 (지급 대상 날짜)

    @Column(name = "created_dmt", nullable = false, updatable = false)
    private LocalDateTime createdDmt;  // 생성 시간 (요청 시점)

    @Column(name = "update_dtm", nullable = false)
    private LocalDateTime updateDtm;  // 수정 시간

}