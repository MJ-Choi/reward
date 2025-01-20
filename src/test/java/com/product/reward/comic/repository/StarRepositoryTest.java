package com.product.reward.comic.repository;

import com.product.reward.artist.dto.ComicState;
import com.product.reward.comic.dto.OrderDto;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.TestConfig;
import com.product.reward.entity.*;
import com.product.reward.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, FixedTimeConfig.class})
class StarRepositoryTest {

    @Autowired
    JPAQueryFactory factory;
    @Autowired
    Clock clock;

    StarRepository repository;

    long cid = 1;
    long aid = 1;
    long mid = 10;
    String comicName = "star-test";

    @BeforeEach
    void init() {
        DateUtils dateUtils = new DateUtils(clock);
        repository = new StarRepository(factory, dateUtils);

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
    }

    @Test
    void insertStar() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCid(cid);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        boolean result = repository.insertStar(orderDto);
        Assertions.assertTrue(result);

        QStar qStar = QStar.star;
        List<Star> fetch = factory.select(qStar)
                .from(qStar)
                .where(qStar.mid.eq(mid))
                .where(qStar.cid.eq(cid))
                .where(qStar.episod.eq(12))
                .fetch();
        Assertions.assertTrue(fetch.size() >= 1);
    }

    @Test
    void deleteStar() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCid(cid);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        boolean query = repository.insertStar(orderDto);
        Assertions.assertTrue(query);
        boolean result = repository.deleteStar(orderDto);

        QStar qStar = QStar.star;
        List<Star> fetch = factory.select(qStar)
                .from(qStar)
                .where(qStar.mid.eq(mid))
                .where(qStar.cid.eq(cid))
                .where(qStar.episod.eq(12))
                .fetch();
        Assertions.assertTrue(fetch.isEmpty());
    }
}