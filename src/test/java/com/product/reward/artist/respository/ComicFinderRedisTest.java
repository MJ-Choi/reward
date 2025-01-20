package com.product.reward.artist.respository;

import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.dto.ComicState;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.redis.RedisConfig;
import com.product.reward.config.redis.RedisConfiguration;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

class ComicFinderRedisTest {

    ComicFinderRedis finder;
    RedisUtils redisUtils;

    long aid = 10;
    long cid3 = 3;
    long cid4 = 4;
    String comicName3 = "brng";

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
        Clock clock = new FixedTimeConfig().fixedClock();
        CollectRedisKey key = new CollectRedisKey(new DateUtils(clock));
        this.finder = new ComicFinderRedis(key, redisUtils);
        // 레디스 데이터 적재
        redisUtils.setStr("comics", "{" + comicName3 + ":" + cid3 + ",brsv:" + cid4 + "}");
        redisUtils.setStr("artist:".concat(String.valueOf(aid)),
                "{\"" + cid3 + "\":{\"cid\":" + cid3 +
                        ",\"aid\":" + aid +
                        ",\"name\":\"" + comicName3 +
                        "\",\"state\":\"OPEN\"," +
                        "\"dailyViewCnt\":10," +
                        "\"weeklyViewCnt\":10," +
                        "\"monthlyViewCnt\":20," +
                        "\"yearlyViewCnt\":30," +
                        "\"dailyLikeCnt\":0," +
                        "\"weeklyLikeCnt\":2," +
                        "\"monthlyLikeCnt\":3," +
                        "\"yearlyLikeCnt\":5}," +
                        "\"" + cid4 + "\":{\"cid\":" + cid4 + ",\"aid\":" + aid + ",\"name\":\"brsv\",\"state\":\"RESERVED\",\"dailyViewCnt\":0,\"weeklyViewCnt\":0,\"monthlyViewCnt\":0,\"yearlyViewCnt\":0,\"dailyLikeCnt\":0,\"weeklyLikeCnt\":0,\"monthlyLikeCnt\":0,\"yearlyLikeCnt\":0}}");
    }

    @Test
    @DisplayName("단건 정보 조회")
    void getComicInfoWithAidAndComicName() {
        ComicDto comicInfo = finder.getComicInfo(aid, comicName3);
        System.out.println("comicInfo = " + comicInfo);

        Assertions.assertNotNull(comicInfo);
        Assertions.assertEquals(cid3, comicInfo.getCid());
        Assertions.assertEquals(aid, comicInfo.getAid());
        Assertions.assertEquals(comicName3, comicInfo.getName());
        Assertions.assertEquals(ComicState.OPEN, comicInfo.getState());
        Assertions.assertEquals(10, comicInfo.getDailyViewCnt());
        Assertions.assertEquals(10, comicInfo.getWeeklyViewCnt());
        Assertions.assertEquals(20, comicInfo.getMonthlyViewCnt());
        Assertions.assertEquals(30, comicInfo.getYearlyViewCnt());
        Assertions.assertEquals(0, comicInfo.getDailyLikeCnt());
        Assertions.assertEquals(2, comicInfo.getWeeklyLikeCnt());
        Assertions.assertEquals(3, comicInfo.getMonthlyLikeCnt());
        Assertions.assertEquals(5, comicInfo.getYearlyLikeCnt());
    }

    @Test
    @DisplayName("작가ID로 작품 조회")
    void getComicInfosWithAid() {
        List<ComicDto> list = finder.getComicInfos(aid, 0L, 2);
        System.out.println("list = " + list);

        Assertions.assertEquals(2, list.size());
        ComicDto comicInfo = list.get(0);
        Assertions.assertNotNull(comicInfo);
        Assertions.assertEquals(cid3, comicInfo.getCid());
        Assertions.assertEquals(aid, comicInfo.getAid());
        Assertions.assertEquals(comicName3, comicInfo.getName());
        Assertions.assertEquals(ComicState.OPEN, comicInfo.getState());
        Assertions.assertEquals(10, comicInfo.getDailyViewCnt());
        Assertions.assertEquals(10, comicInfo.getWeeklyViewCnt());
        Assertions.assertEquals(20, comicInfo.getMonthlyViewCnt());
        Assertions.assertEquals(30, comicInfo.getYearlyViewCnt());
        Assertions.assertEquals(0, comicInfo.getDailyLikeCnt());
        Assertions.assertEquals(2, comicInfo.getWeeklyLikeCnt());
        Assertions.assertEquals(3, comicInfo.getMonthlyLikeCnt());
        Assertions.assertEquals(5, comicInfo.getYearlyLikeCnt());
    }

    @Test
    @DisplayName("작가의 총 작품수 조회")
    void getComicCount() {
        Long comicCount = finder.getComicCount(aid);
        Assertions.assertEquals(2, comicCount);
    }
}