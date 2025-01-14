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
@Table(name = "comics")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;  // 작품 ID

    @Column(name = "aid", nullable = false)
    private Long aid;  // 작가 ID

    @Column(name = "comic_name", nullable = false)
    private String name;  // 작품 이름

    @Column(name = "state", nullable = false)
    private String state;  // 작품 상태

    @Column(name = "created_dmt", nullable = false, updatable = false)
    private LocalDateTime createdDmt;  // 생성 시간

    @Column(name = "update_dtm", nullable = false)
    private LocalDateTime updateDtm;  // 수정 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aid", nullable = false, insertable = false, updatable = false)
    private Artist artist;

}