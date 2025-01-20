package com.product.reward.artist.respository;

import com.product.reward.config.redis.RedisConfig;
import com.product.reward.config.redis.RedisConfiguration;
import com.product.reward.util.RedisUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistFinderRedisTest {

    private ArtistFinderRedis finder;
    private RedisUtils redisUtils;

    @BeforeEach
    void init() {
        RedisConfiguration config = new RedisConfiguration();
        config.setServer("localhost");
        config.setPort(6379);
        config.setDatabase(0);
        RedisConfig redisConfig = new RedisConfig(config);
        redisUtils = new RedisUtils(redisConfig.stringRedisTemplate(), config);
        // 레디스 초기화
        redisUtils.flushdb();
        this.finder = new ArtistFinderRedis(redisUtils);
    }

    @Test
    void isExist() {
        String redisKey = "artist";
        String value = "{1:”artist-a”,2:”artist-b\"}";
        redisUtils.setStr(redisKey, value);

        boolean result = finder.isExist(1L);
        Assertions.assertTrue(result);
    }
}