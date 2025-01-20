package com.product.reward.artist.respository;

import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.dto.ComicState;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.TestConfig;
import com.product.reward.entity.*;
import com.product.reward.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.util.List;

@Slf4j
@DataJpaTest
@EnableAspectJAutoProxy
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, FixedTimeConfig.class})
class ComicFinderRepositoryTest {

    ComicFinderRepository repository;
    DateUtils dateUtils;

    @Autowired
    JPAQueryFactory factory;

    @Autowired
    Clock clock;

    long cid = 1;
    long aid = 1;
    long mid = 10;
    String comicName = "test-comic";

    @BeforeEach
    void init() {
        dateUtils = new DateUtils(clock); //"2024-12-12T12:12:12Z"
        this.repository = new ComicFinderRepository(factory, dateUtils);
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
        // order
        QOrder qOrder = QOrder.order;
        factory.insert(qOrder)
                .columns(
                        qOrder.cid,
                        qOrder.mid,
                        qOrder.episod,
                        qOrder.createdDmt
                )
                .values(
                        cid,
                        mid,
                        10,
                        dateUtils.now().plusHours(1)
                )
                .execute();

        // star
        QStar qStar = QStar.star;
        factory.insert(qStar)
                .columns(
                        qStar.mid,
                        qStar.cid,
                        qStar.episod,
                        qStar.createdDtm
                )
                .values(
                        mid,
                        cid,
                        10,
                        dateUtils.now().plusHours(1)
                )
                .execute();
    }

    @Test
    void getComicInfoWithAidAndCname() {
        ComicDto comicInfo = repository.getComicInfo(aid, comicName);
        System.out.println("comicInfo = " + comicInfo);

        Assertions.assertNotNull(comicInfo);
        Assertions.assertEquals(cid, comicInfo.getCid());
        Assertions.assertEquals(aid, comicInfo.getAid());
        Assertions.assertEquals(comicName, comicInfo.getName());
        Assertions.assertEquals(ComicState.OPEN, comicInfo.getState());
        Assertions.assertEquals(1, comicInfo.getDailyViewCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyViewCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getDailyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyLikeCnt());
    }

    @Test
    @DisplayName("전체 조회")
    void getComicInfos() {
        List<ComicDto> list = repository.getComicInfos(aid);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());

        ComicDto comicInfo = list.get(0);
        Assertions.assertNotNull(comicInfo);
        Assertions.assertEquals(cid, comicInfo.getCid());
        Assertions.assertEquals(aid, comicInfo.getAid());
        Assertions.assertEquals(comicName, comicInfo.getName());
        Assertions.assertEquals(ComicState.OPEN, comicInfo.getState());
        Assertions.assertEquals(1, comicInfo.getDailyViewCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyViewCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getDailyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyLikeCnt());
    }

    @Test
    void getComicInfosWithAid() {
        List<ComicDto> list = repository.getComicInfos(aid, 0, 10);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());

        ComicDto comicInfo = list.get(0);
        Assertions.assertNotNull(comicInfo);
        Assertions.assertEquals(cid, comicInfo.getCid());
        Assertions.assertEquals(aid, comicInfo.getAid());
        Assertions.assertEquals(comicName, comicInfo.getName());
        Assertions.assertEquals(ComicState.OPEN, comicInfo.getState());
        Assertions.assertEquals(1, comicInfo.getDailyViewCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyViewCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyViewCnt());
        Assertions.assertEquals(1, comicInfo.getDailyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getWeeklyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getMonthlyLikeCnt());
        Assertions.assertEquals(1, comicInfo.getYearlyLikeCnt());

    }

    @Test
    void getComicCount() {
        Long comicCount = repository.getComicCount(aid);
        Assertions.assertEquals(1, comicCount);
    }
}