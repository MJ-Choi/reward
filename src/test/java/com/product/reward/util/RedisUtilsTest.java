package com.product.reward.util;

import com.product.reward.config.redis.RedisConfig;
import com.product.reward.config.redis.RedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

class RedisUtilsTest {

    private RedisUtils redisUtils;

    @BeforeEach
    void init() {
        RedisConfiguration config = new RedisConfiguration();
        config.setServer("localhost");
        config.setPort(6379);
        config.setDatabase(0);
        RedisConfig redisConfig = new RedisConfig(config);
        this.redisUtils = new RedisUtils(redisConfig.stringRedisTemplate(), config);
    }

    @Test
    void getStr() {
        String key = "test-get";
        String value = "get-value";
        redisUtils.setStr(key, value);
        String result = redisUtils.getStr(key);
        System.out.println("result = " + result);
        Assertions.assertFalse(StringUtils.isBlank(result));
        Assertions.assertEquals(value, result);

        redisUtils.remove(key);
    }

    @Test
    void setStr() {
        String key = "set-test";
        String value = "set-value";
        boolean result = redisUtils.setStr(key, value);
        Assertions.assertTrue(result);

        redisUtils.remove(key);
    }

    @Test
    @DisplayName("레디스 키 삭제")
    void remove() {
        String key = "d-key";
        String value = "tmp";
        redisUtils.setStr(key, value);
        Assertions.assertTrue(redisUtils.remove(key));
    }

    @Test
    @DisplayName("레디스에 없는 데이터 삭제")
    void removeEmptyData() {
        String key = "null-key";
        Assertions.assertFalse(redisUtils.remove(key));
    }
}