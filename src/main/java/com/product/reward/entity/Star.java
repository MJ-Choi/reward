package com.product.reward.entity;

import com.product.reward.entity.id.StarId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Column(name = "created_dtm", nullable = false)
    private LocalDateTime createdDtm;  // 좋아요 생성 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cid", nullable = false, insertable = false, updatable = false)
    private Comic comic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", nullable = false)
    private Member member;

}