package com.product.reward.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RewardId implements Serializable {
    private Long rid;  // 보상 ID
    private Long cid;  // 만화 ID
    private String type;  // 작가 혹은 소비자
    private Long id;  // 리워드를 받은 작가 혹은 소비자 ID

}