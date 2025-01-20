package com.product.reward.reward.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectBaseDto {
    private Long cid;

    private Integer dView;
    private Integer wView;
    private Integer mView;
    private Integer yView;

    private Integer dLike;
    private Integer wLike;
    private Integer mLike;
    private Integer yLike;

}