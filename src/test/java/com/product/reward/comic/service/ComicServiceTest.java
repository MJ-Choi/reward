package com.product.reward.comic.service;

import com.product.reward.artist.dto.ComicState;
import com.product.reward.comic.dto.OrderDto;
import com.product.reward.comic.repository.OrderRepository;
import com.product.reward.comic.repository.RedisCountCollector;
import com.product.reward.comic.repository.StarRepository;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.TestConfig;
import com.product.reward.config.redis.RedisConfig;
import com.product.reward.config.redis.RedisConfiguration;
import com.product.reward.entity.*;
import com.product.reward.reward.repository.RankFinderRepository;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.util.Map;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, FixedTimeConfig.class})
class ComicServiceTest {

    @Autowired
    JPAQueryFactory factory;
    @Autowired
    Clock clock;
    OrderRepository orderRepository;
    StarRepository starRepository;

    ComicService comicService;
    RedisUtils redisUtils;
    DateUtils dateUtils;
    RankFinderRepository rankFinderRepository;

    long cid = 1;
    long aid = 1;
    long mid = 10;
    String comicName = "comicService-test";

    @BeforeEach
    void init() {
        dateUtils = new DateUtils(clock);
        RedisConfiguration config = new RedisConfiguration();
        config.setServer("localhost");
        config.setPort(6380);
        config.setDatabase(0);
        RedisConfig redisConfig = new RedisConfig(config);
        redisUtils = new RedisUtils(redisConfig.stringRedisTemplate(), config);
        RedisCountCollector collector = new RedisCountCollector(redisUtils, dateUtils);
        rankFinderRepository = new RankFinderRepository(factory, dateUtils);
        orderRepository = new OrderRepository(factory, dateUtils);
        starRepository = new StarRepository(factory, dateUtils);
        comicService = new ComicService(orderRepository, starRepository, collector, clock, rankFinderRepository);


        // artist
        QArtist qArtist = QArtist.artist;
        factory.insert(qArtist)
                .columns(
                        qArtist.aid,
                        qArtist.name
                )
                .values(
                        aid,
                        "test-artist"
                )
                .execute();
        // comic
        QComic qComic = QComic.comic;
        factory.insert(qComic)
                .columns(
                        qComic.cid,
                        qComic.aid,
                        qComic.name,
                        qComic.state,
                        qComic.createdDmt,
                        qComic.updateDtm
                )
                .values(
                        cid,
                        aid,
                        comicName,
                        ComicState.OPEN.getCode(),
                        dateUtils.now(),
                        dateUtils.now()
                ).execute();
        // uesr
        QMember qMember = QMember.member;
        factory.insert(qMember)
                .columns(qMember.mid, qMember.userName)
                .values(mid, "test-mem")
                .execute();

        redisUtils.setStr("comics", Map.of(comicName, cid));
    }

    @Test
    void addViewCount() {
        OrderDto orderDto = new OrderDto();
        orderDto.setComicName(comicName);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        boolean result = comicService.addViewCount(orderDto);
        Assertions.assertTrue(result);

        // 2024-12-12T00:00 - 2024-12-12T23:59:59.999999999
        Map<Long, Integer> rankMap = rankFinderRepository.dailyTopComics(10);
        QOrder qOrder = QOrder.order;
        Order order = factory.select(qOrder).from(qOrder)
                .where(qOrder.cid.eq(cid))
                .where(qOrder.mid.eq(mid))
                .where(qOrder.episod.eq(12))
                .fetchOne();
        System.out.println("order = " + order); // createdDmt=2024-12-12T21:12:12
        System.out.println("rankMap = " + rankMap);
        Assertions.assertNotNull(rankMap);
        Integer integer = rankMap.get(cid);
        Assertions.assertTrue(integer >= 1);
    }

    @Test
    @DisplayName("view 없이 like만 존재하는 것은 프로세스 상 불가함을 전제함")
    void addViewLikeCount() {
        OrderDto orderDto = new OrderDto();
        orderDto.setComicName(comicName);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        boolean result = comicService.addLikeCount(orderDto) &&
                comicService.addViewCount(orderDto);
        Assertions.assertTrue(result);

        // 2024-12-09T21:12:12 - 2024-12-12T23:59:59.999999999
        Map<Long, Integer> rankMap = rankFinderRepository.weeklyTopComics(10);
        QStar qStar = QStar.star;
        Star star = factory.select(qStar).from(qStar)
                .where(qStar.cid.eq(cid))
                .where(qStar.mid.eq(mid))
                .where(qStar.episod.eq(12))
                .fetchOne();
        System.out.println("star = " + star); // createdDtm=2024-12-12T21:12:12
        System.out.println("rankMap = " + rankMap);
        Assertions.assertNotNull(rankMap);
        Integer score = rankMap.get(cid);
        System.out.println("integer = " + score);
        Assertions.assertTrue(score >= 3);
    }

    @Test
    void subLikeCount() {
        OrderDto orderDto = new OrderDto();
        orderDto.setComicName(comicName);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        comicService.addLikeCount(orderDto);
        comicService.addViewCount(orderDto);

        Map<Long, Integer> rankMap = rankFinderRepository.weeklyTopComics(10);
        System.out.println("rankADDMap = " + rankMap);
        Assertions.assertNotNull(rankMap);
        Integer score = rankMap.get(cid);
        System.out.println("afterAdd:integer = " + score);
        Assertions.assertTrue(score >= 3);

        boolean result = comicService.subLikeCount(orderDto);
        Assertions.assertTrue(result);
        QStar qStar = QStar.star;
        Star star = factory.select(qStar).from(qStar)
                .where(qStar.cid.eq(cid))
                .where(qStar.mid.eq(mid))
                .where(qStar.episod.eq(12))
                .fetchOne();
        Assertions.assertNull(star);
        QOrder qOrder = QOrder.order;
        Order order = factory.select(qOrder).from(qOrder)
                .where(qOrder.cid.eq(cid))
                .where(qOrder.mid.eq(mid))
                .where(qOrder.episod.eq(12))
                .fetchOne();
        System.out.println("order = " + order);

        Map<Long, Integer> subRankMap = rankFinderRepository.weeklyTopComics(10);
        System.out.println("rankSUBMap = " + subRankMap);
        Assertions.assertNotNull(subRankMap);
        Integer subInt = subRankMap.get(cid);
        System.out.println("afterSub:integer = " + subInt);
        Assertions.assertTrue(subInt >= 1);
    }
}