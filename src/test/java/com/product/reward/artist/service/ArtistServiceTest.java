package com.product.reward.artist.service;

import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.dto.ComicState;
import com.product.reward.artist.dto.request.ComicEditRequest;
import com.product.reward.artist.dto.request.ComicRegisterRequest;
import com.product.reward.artist.respository.*;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.TestConfig;
import com.product.reward.config.redis.RedisConfig;
import com.product.reward.config.redis.RedisConfiguration;
import com.product.reward.entity.QArtist;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@DataJpaTest
@EnableAspectJAutoProxy
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, FixedTimeConfig.class,
        ComicRepository.class})
class ArtistServiceTest {

    @Autowired
    JPAQueryFactory factory;
    @Autowired
    Clock clock;
    @Autowired
    ComicRepository comicRepository;
    ArtistService artistService;
    RedisUtils redisUtils;

    long aid = 1;

    @BeforeEach
    void init() {
        DateUtils dateUtils = new DateUtils(clock);
        RedisConfiguration config = new RedisConfiguration();
        config.setServer("localhost");
        config.setPort(6380);
        config.setDatabase(0);
        RedisConfig redisConfig = new RedisConfig(config);
        redisUtils = new RedisUtils(redisConfig.stringRedisTemplate(), config);
        redisUtils.flushdb();
        ArtistFinder artistFinder = new ArtistFinderRedis(redisUtils);
        ComicFinder comicFinder = new ComicFinderRedis(new CollectRedisKey(dateUtils), redisUtils);
        this.artistService = new ArtistService(
                comicRepository,
                artistFinder,
                comicFinder,
                redisUtils,
                new CollectRedisKey(dateUtils)
        );
        QArtist qArtist = QArtist.artist;
        factory.insert(qArtist)
                .columns(qArtist.aid, qArtist.name)
                .values(aid, "artist-a")
                .execute();
        String redisKey = "artist";
        String value = "{" + aid + ":”artist-a”,2:”artist-b\"}";
        redisUtils.setStr(redisKey, value);
    }

    @Test
    void register() {
        String comicName = "test-comic";
        ComicRegisterRequest request = new ComicRegisterRequest();
        request.setComicName(comicName);
        request.setState("02");

        ComicDto result = artistService.register(1L, request);
        System.out.println("result = " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCid() > 0);
        Assertions.assertEquals(aid, result.getAid());
        Assertions.assertEquals(comicName, result.getName());
        Assertions.assertEquals(ComicState.OPEN, result.getState());
        Assertions.assertEquals(0, result.getDailyViewCnt());
        Assertions.assertEquals(0, result.getWeeklyViewCnt());
        Assertions.assertEquals(0, result.getMonthlyViewCnt());
        Assertions.assertEquals(0, result.getYearlyViewCnt());
        Assertions.assertEquals(0, result.getDailyLikeCnt());
        Assertions.assertEquals(0, result.getWeeklyLikeCnt());
        Assertions.assertEquals(0, result.getMonthlyLikeCnt());
        Assertions.assertEquals(0, result.getYearlyLikeCnt());
    }

    @Test
    void getInfos() {
        String comicName = "test-comic";
        ComicRegisterRequest request = new ComicRegisterRequest();
        request.setComicName(comicName);
        request.setState("02");
        artistService.register(aid, request);

        String comicName2 = "test-comic2";
        ComicRegisterRequest request2 = new ComicRegisterRequest();
        request2.setComicName(comicName2);
        artistService.register(aid, request2);

        LinkedHashMap<Long, ComicDto> linkedMap = redisUtils.getLinkedMap("artist:" + aid, Long.class, ComicDto.class);
        System.out.println("linkedMap = " + linkedMap);
        Assertions.assertNotNull(linkedMap);

        Page<ComicDto> pages = artistService.getInfos(aid, PageRequest.of(0, 5));
        Assertions.assertNotNull(pages);

        List<ComicDto> list = pages.get().toList();
        System.out.println("list = " + list);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() >= 2);

        ComicDto result = list.get(list.size() - 2);
        System.out.println("result = " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCid() > 0);
        Assertions.assertEquals(aid, result.getAid());
        Assertions.assertEquals(comicName, result.getName());
        Assertions.assertEquals(ComicState.OPEN, result.getState());
        Assertions.assertEquals(0, result.getDailyViewCnt());
        Assertions.assertEquals(0, result.getWeeklyViewCnt());
        Assertions.assertEquals(0, result.getMonthlyViewCnt());
        Assertions.assertEquals(0, result.getYearlyViewCnt());
        Assertions.assertEquals(0, result.getDailyLikeCnt());
        Assertions.assertEquals(0, result.getWeeklyLikeCnt());
        Assertions.assertEquals(0, result.getMonthlyLikeCnt());
        Assertions.assertEquals(0, result.getYearlyLikeCnt());

        ComicDto info = list.get(list.size() - 1);
        System.out.println("info = " + info);
        Assertions.assertNotNull(info);
        Assertions.assertTrue(info.getCid() > 0);
        Assertions.assertEquals(aid, info.getAid());
        Assertions.assertEquals(comicName2, info.getName());
        Assertions.assertEquals(ComicState.RESERVED, info.getState());
        Assertions.assertEquals(0, info.getDailyViewCnt());
        Assertions.assertEquals(0, info.getWeeklyViewCnt());
        Assertions.assertEquals(0, info.getMonthlyViewCnt());
        Assertions.assertEquals(0, info.getYearlyViewCnt());
        Assertions.assertEquals(0, info.getDailyLikeCnt());
        Assertions.assertEquals(0, info.getWeeklyLikeCnt());
        Assertions.assertEquals(0, info.getMonthlyLikeCnt());
        Assertions.assertEquals(0, info.getYearlyLikeCnt());
    }

    @Test
    void getInfo() {
        String comicName = "test-comic";
        ComicRegisterRequest request = new ComicRegisterRequest();
        request.setComicName(comicName);
        request.setState("02");
        artistService.register(1L, request);
        LinkedHashMap<Long, ComicDto> linkedMap = redisUtils.getLinkedMap("artist:" + aid, Long.class, ComicDto.class);
        System.out.println("linkedMap = " + linkedMap);
        Assertions.assertNotNull(linkedMap);

        ComicDto result = artistService.getInfo(aid, comicName);
        System.out.println("result = " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCid() > 0);
        Assertions.assertEquals(aid, result.getAid());
        Assertions.assertEquals(comicName, result.getName());
        Assertions.assertEquals(ComicState.OPEN, result.getState());
        Assertions.assertEquals(0, result.getDailyViewCnt());
        Assertions.assertEquals(0, result.getWeeklyViewCnt());
        Assertions.assertEquals(0, result.getMonthlyViewCnt());
        Assertions.assertEquals(0, result.getYearlyViewCnt());
        Assertions.assertEquals(0, result.getDailyLikeCnt());
        Assertions.assertEquals(0, result.getWeeklyLikeCnt());
        Assertions.assertEquals(0, result.getMonthlyLikeCnt());
        Assertions.assertEquals(0, result.getYearlyLikeCnt());
    }

    @Test
    void editState() {
        String comicName = "edit-comic";
        ComicRegisterRequest registerReq = new ComicRegisterRequest();
        registerReq.setComicName(comicName);
        registerReq.setState("02");
        ComicDto comicDto = artistService.register(aid, registerReq);

        ComicEditRequest request = new ComicEditRequest();
        ComicEditRequest.EditRequest editDto = new ComicEditRequest.EditRequest();
        editDto.setCid(comicDto.getCid());
        editDto.setState("03");
        request.setEdits(List.of(editDto));
        boolean edit = artistService.edit(aid, request);
        Assertions.assertTrue(edit);

        ComicDto result = artistService.getInfo(aid, comicName);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(comicDto.getCid(), result.getCid());
        Assertions.assertEquals(aid, result.getAid());
        Assertions.assertEquals(comicName, result.getName());
        Assertions.assertEquals(ComicState.SLEEP, result.getState());
        Assertions.assertEquals(0, result.getDailyViewCnt());
        Assertions.assertEquals(0, result.getWeeklyViewCnt());
        Assertions.assertEquals(0, result.getMonthlyViewCnt());
        Assertions.assertEquals(0, result.getYearlyViewCnt());
        Assertions.assertEquals(0, result.getDailyLikeCnt());
        Assertions.assertEquals(0, result.getWeeklyLikeCnt());
        Assertions.assertEquals(0, result.getMonthlyLikeCnt());
        Assertions.assertEquals(0, result.getYearlyLikeCnt());
    }

    @Test
    void editName() {
        String comicName = "edit-comic";
        ComicRegisterRequest registerReq = new ComicRegisterRequest();
        registerReq.setComicName("test-name");
        registerReq.setState("02");
        ComicDto comicDto = artistService.register(aid, registerReq);

        ComicEditRequest request = new ComicEditRequest();
        ComicEditRequest.EditRequest editDto = new ComicEditRequest.EditRequest();
        editDto.setCid(comicDto.getCid());
        editDto.setComicName(comicName);
        request.setEdits(List.of(editDto));
        boolean edit = artistService.edit(aid, request);
        Assertions.assertTrue(edit);

        ComicDto result = artistService.getInfo(aid, comicName);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(comicDto.getCid(), result.getCid());
        Assertions.assertEquals(aid, result.getAid());
        Assertions.assertEquals(comicName, result.getName());
        Assertions.assertEquals(ComicState.OPEN, result.getState());
        Assertions.assertEquals(0, result.getDailyViewCnt());
        Assertions.assertEquals(0, result.getWeeklyViewCnt());
        Assertions.assertEquals(0, result.getMonthlyViewCnt());
        Assertions.assertEquals(0, result.getYearlyViewCnt());
        Assertions.assertEquals(0, result.getDailyLikeCnt());
        Assertions.assertEquals(0, result.getWeeklyLikeCnt());
        Assertions.assertEquals(0, result.getMonthlyLikeCnt());
        Assertions.assertEquals(0, result.getYearlyLikeCnt());
    }
}