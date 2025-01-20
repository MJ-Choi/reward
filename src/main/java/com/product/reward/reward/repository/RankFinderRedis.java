package com.product.reward.reward.repository;

import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RankFinderRedis implements RankFinder {

    private final CollectRedisKey collectKey;
    private final RedisUtils redisUtils;
    private Map<String, Long> idMapper;

    public RankFinderRedis(CollectRedisKey collectKey, RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
        this.collectKey = collectKey;
        this.idMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
    }

    @Override
    public Map<Long, Integer> dailyTopComics(int top) {
        Map<String, Integer> viewMap = redisUtils.getMap(collectKey.getDayCollectKey(), String.class, Integer.class);
        Map<String, Integer> likeMap = redisUtils.getMap(collectKey.getDayLikeKey(), String.class, Integer.class);
        return getRank(top, viewMap, likeMap);
    }

    @Override
    public Map<Long, Integer> weeklyTopComics(int top) {
        Map<String, Integer> viewMap = redisUtils.getMap(collectKey.getWeekCollectKey(), String.class, Integer.class);
        Map<String, Integer> likeMap = redisUtils.getMap(collectKey.getWeekLikeKey(), String.class, Integer.class);
        return getRank(top, viewMap, likeMap);
    }

    @Override
    public Map<Long, Integer> monthlyTopComics(int top) {
        Map<String, Integer> viewMap = redisUtils.getMap(collectKey.getMonthCollectKey(), String.class, Integer.class);
        Map<String, Integer> likeMap = redisUtils.getMap(collectKey.getMonthLikeKey(), String.class, Integer.class);
        return getRank(top, viewMap, likeMap);
    }

    @Override
    public Map<Long, Integer> yearlyTopComics(int top) {
        Map<String, Integer> viewMap = redisUtils.getMap(collectKey.getYearCollectKey(), String.class, Integer.class);
        Map<String, Integer> likeMap = redisUtils.getMap(collectKey.getYearLikeKey(), String.class, Integer.class);
        return getRank(top, viewMap, likeMap);
    }

    public Long cidByName(String comicName) {
        if (!this.idMapper.containsKey(comicName)) {
            this.idMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
        }
        return this.idMapper.getOrDefault(comicName, 0L);
    }

    private Map<Long, Integer> getRank(int top, Map<String, Integer> viewMap, Map<String, Integer> likeMap) {
        Map<Long, Integer> rankMap = new HashMap<>();
        // update comics
        this.idMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
        // 랭킹 점수 = (좋아요 * 2) + (조회수 * 1)
        this.idMapper.keySet()
                .forEach(comic -> rankMap.put(cidByName(comic),
                        likeMap.getOrDefault(comic, 0) * 2 + viewMap.getOrDefault(comic, 0)));
        // 높은 점수로 정렬
        List<Long> list = rankMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
        Map<Long, Integer> result = new HashMap<>();

        int value = rankMap.get(list.get(top - 1)); //10위 점수
        for (int i = top; i < list.size(); i++) {
            Long cid = list.get(i);
            if (value <= rankMap.get(cid)) {
                result.put(cid, rankMap.get(cid));
            } else {
                // 더이상 동점자가 없다면 for문 종료
                break;
            }
        }
        return result;
    }
}
