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
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aid;  // 작가 ID

    @Column(name = "artist_name", nullable = false)
    private String name;  // 작가 이름

    @Column(name = "created_dtm", nullable = false, updatable = false)
    private LocalDateTime createdDtm;  // 생성 시간

    @Column(name = "update_dtm", nullable = false)
    private LocalDateTime updateDtm;  // 수정 시간

}