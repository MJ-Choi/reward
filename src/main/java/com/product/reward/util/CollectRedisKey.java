package com.product.reward.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CollectRedisKey {

    private final DateUtils dateUtils;

    /**
     * 일별 집계 키
     * @return view-collect:yyyyMMdd
     */
    public String getDayCollectKey() {
        return "view-collect:".concat(dateUtils.getDay());
    }

    /**
     * 주별 집계 키
     * @return view-collect:wyyyyMMdd
     */
    public String getWeekCollectKey() {
        return "view-collect:w".concat(dateUtils.getDay());
    }

    /**
     * 월별 집계 키
     * @return view-collect:yyyyMM
     */
    public String getMonthCollectKey() {
        return "view-collect:".concat(dateUtils.getMonth());
    }

    /**
     * 연도별 집계 키
     * @return view-collect:yyyy
     */
    public String getYearCollectKey() {
        return "view-collect:".concat(dateUtils.getYear());
    }

    /**
     * 일별 집계 키
     * @return like-collect:yyyyMMdd
     */
    public String getDayLikeKey() {
        return "like-collect:".concat(dateUtils.getDay());
    }

    /**
     * 주별 집계 키
     * @return like-collect:wyyyyMMdd
     */
    public String getWeekLikeKey() {
        return "view-collect:w".concat(dateUtils.getDay());
    }

    /**
     * 월별 집계 키
     * @return like-collect:yyyyMM
     */
    public String getMonthLikeKey() {
        return "like-collect:".concat(dateUtils.getMonth());
    }

    /**
     * 연도별 집계 키
     * @return like-collect:yyyy
     */
    public String getYearLikeKey() {
        return "like-collect:".concat(dateUtils.getYear());
    }

    /**
     * comicName-cid 매핑 키
     * key: comics
     * value: (Map) { comicName: {cid} }
     * @return comics
     */
    public String getComicMapKey() {
        return "comics";
    }
}
