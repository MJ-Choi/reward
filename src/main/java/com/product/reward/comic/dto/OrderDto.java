package com.product.reward.comic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDto {

    private Long cid;
    private String comicName;
    private Long mid;
    private int episod;

    public OrderDto(String comicName, Long mid, int episod) {
        this.comicName = comicName;
        this.mid = mid;
        this.episod = episod;
    }
}
