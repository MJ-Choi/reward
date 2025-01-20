package com.product.reward.artist.respository;

import com.product.reward.util.ArtistRedisKey;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ArtistFinderRedis implements ArtistFinder{

    private final RedisUtils redisUtils;
    private final ArtistRedisKey redisKey;

    public ArtistFinderRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
        this.redisKey = new ArtistRedisKey();
    }

    @Override
    public boolean isExist(Long aid) {
        Map<Long, String> map = redisUtils.getMap(redisKey.artistListKey(), Long.class, String.class);
        return map.containsKey(aid);
    }
}
