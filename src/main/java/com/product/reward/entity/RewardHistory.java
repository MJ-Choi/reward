package com.product.reward.entity;

import com.product.reward.entity.id.RewardId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@IdClass(RewardId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reward_history")
public class RewardHistory {

    @Id
    @Column(name = "rid", nullable = false)
    private Long rid;  // 리워드 ID

    @Id
    @Column(name = "cid", nullable = false)
    private Long cid;  // 작품 ID

    @Id
    @Column(name = "user_type", length = 2, nullable = false)
    private String type;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;  // 작가 혹은 소비자 id

    @Column(name = "rank", nullable = false)
    private int rank; // 지급이유(랭킹순위)

    @Column(name = "point", nullable = false)
    private int point; // 지급된 포인트 = 랭킹점수

    @Column(name = "created_dmt", columnDefinition = "DATETIME")
    private LocalDateTime createdDmt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid", nullable = false, insertable = false, updatable = false)
    private Reward reward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cid", nullable = false, insertable = false, updatable = false)
    private Comic comic;

}