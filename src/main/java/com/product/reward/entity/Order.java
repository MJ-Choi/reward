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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oid;  // 주문 ID

    @Column(name = "cid", nullable = false)
    private Long cid;  // 작품 ID

    @Column(name = "mid", nullable = false)
    private Long mid;  // 소비자 ID

    @Column(name = "episod", nullable = false)
    private Integer episod;  // 회차 (0은 작품 자체, 그 이상은 회차)

    @Column(name = "created_dmt", nullable = false, updatable = false)
    private LocalDateTime createdDmt;  // 주문 생성 시간

    @Column(name = "update_dtm", nullable = false)
    private LocalDateTime updateDtm;  // 주문 수정 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cid", nullable = false, insertable = false, updatable = false)
    private Comic comic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", nullable = false, insertable = false, updatable = false)
    private Member member;
}