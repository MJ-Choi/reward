package com.product.reward.entity.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StarId implements Serializable {

    private Long mid;
    private Long cid;

}