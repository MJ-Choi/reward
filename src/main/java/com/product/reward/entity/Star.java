package com.product.reward.entity;

import com.product.reward.entity.id.StarId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@IdClass(StarId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stars")
public class Star {

    @Id
    @Column(name = "cid", nullable = false)
    private Long cid;  // 작품 ID

    @Id
    @Column(name = "mid", nullable = false)
    private Long mid;  // 소비자 ID

    @Id
    @Column(name = "episod", nullable = false)
    private Integer episod;  // 회차(0이면 작품 자체, 1 이상의 자연수는 회차)

    @Column(name = "created_dtm", nullable = false)
    private LocalDateTime createdDtm;  // 좋아요 생성 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cid", nullable = false, insertable = false, updatable = false)
    private Comic comic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", nullable = false)
    private Member member;

}