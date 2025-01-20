package com.product.reward.artist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComicDto {

    private long cid = 0;
    private long aid = 0;
    private String name;
    private ComicState state;

    private Integer dailyViewCnt = 0;
    private Integer weeklyViewCnt = 0;
    private Integer monthlyViewCnt = 0;
    private Integer yearlyViewCnt = 0;

    private Integer dailyLikeCnt = 0;
    private Integer weeklyLikeCnt = 0;
    private Integer monthlyLikeCnt = 0;
    private Integer yearlyLikeCnt = 0;

    public ComicDto(Long cid, Long aid, String name, String state,
                    int dailyViewCnt, int weeklyViewCnt, int monthlyViewCnt, int yearlyViewCnt,
                    int dailyLikeCnt, int weeklyLikeCnt, int monthlyLikeCnt, int yearlyLikeCnt) {
        this.cid = cid;
        this.aid = aid;
        this.name = name;
        this.state = ComicState.fromCode(state);

        this.dailyViewCnt = dailyViewCnt;
        this.weeklyViewCnt = weeklyViewCnt;
        this.monthlyViewCnt = monthlyViewCnt;
        this.yearlyViewCnt = yearlyViewCnt;

        this.dailyLikeCnt = dailyLikeCnt;
        this.weeklyLikeCnt = weeklyLikeCnt;
        this.monthlyLikeCnt = monthlyLikeCnt;
        this.yearlyLikeCnt = yearlyLikeCnt;
    }

    public ComicDto(Long cid, Long aid, String name, String state) {
        this.cid = cid;
        this.aid = aid;
        this.name = name;
        this.state = ComicState.fromCode(state);
    }
}
