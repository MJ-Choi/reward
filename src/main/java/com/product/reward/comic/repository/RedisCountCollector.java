package com.product.reward.comic.repository;

import com.product.reward.reward.dto.CollectBaseDto;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 집계
 */
@Slf4j
@Repository
public class RedisCountCollector {

    private final RedisUtils redisUtils;
    private final CollectRedisKey collectKey;
    private Map<String, Long> idMapper;

    public RedisCountCollector(RedisUtils redisUtils, DateUtils dateUtils) {
        this.redisUtils = redisUtils;
        this.collectKey = new CollectRedisKey(dateUtils);
        this.idMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
    }

    public void addViewCount(String comicName) {
        setToRedis(comicName, collectKey.getDayCollectKey());
        setToRedis(comicName, collectKey.getWeekCollectKey());
        setToRedis(comicName, collectKey.getMonthCollectKey());
        setToRedis(comicName, collectKey.getYearCollectKey());
    }

    public void addLikeCount(String comicName) {
        setToRedis(comicName, collectKey.getDayLikeKey());
        setToRedis(comicName, collectKey.getWeekLikeKey());
        setToRedis(comicName, collectKey.getMonthLikeKey());
        setToRedis(comicName, collectKey.getYearLikeKey());
    }

    public void subLikeCount(String comicName, CollectBaseDto baseDto) {
        setToRedis(comicName, baseDto.getDLike(), collectKey.getDayLikeKey());
        setToRedis(comicName, baseDto.getWLike(), collectKey.getWeekLikeKey());
        setToRedis(comicName, baseDto.getMLike(), collectKey.getMonthLikeKey());
        setToRedis(comicName, baseDto.getYLike(), collectKey.getYearLikeKey());
    }

    /**
     * key: CONSTANT:DTM
     * value: (Map) {comicName: count}
     *
     * @param key
     */
    private void setToRedis(String comicName, String key) {
        Map<String, Integer> countMap = redisUtils.getMap(key, String.class, Integer.class);
        Integer cnt = countMap.getOrDefault(comicName, 0);
        countMap.put(comicName, cnt + 1);
        redisUtils.setStr(key, countMap);
    }

    private void setToRedis(String comicName, int count, String key) {
        Map<String, Integer> countMap = redisUtils.getMap(key, String.class, Integer.class);
        countMap.put(comicName, count);
        redisUtils.setStr(key, countMap);
    }

    public Long cid(String comicName) {
        if (!this.idMapper.containsKey(comicName)) {
            this.idMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
        }
        return this.idMapper.getOrDefault(comicName, 0L);
    }
}
