package com.product.reward.comic.repository;

import com.product.reward.artist.dto.ComicState;
import com.product.reward.comic.dto.OrderDto;
import com.product.reward.config.FixedTimeConfig;
import com.product.reward.config.TestConfig;
import com.product.reward.entity.*;
import com.product.reward.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
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
class OrderRepositoryTest {

    @Autowired
    JPAQueryFactory factory;
    @Autowired
    Clock clock;

    OrderRepository repository;

    long cid = 1;
    long aid = 1;
    long mid = 10;
    String comicName = "order-test";

    @BeforeEach
    void init() {
        DateUtils dateUtils = new DateUtils(clock);
        repository = new OrderRepository(factory, dateUtils);

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
    void insertOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCid(cid);
        orderDto.setMid(mid);
        orderDto.setEpisod(12);
        boolean result = repository.insertOrder(orderDto);
        Assertions.assertTrue(result);

        QOrder qOrder = QOrder.order;
        List<Order> fetch = factory.select(qOrder)
                .from(qOrder)
                .where(qOrder.mid.eq(mid))
                .where(qOrder.cid.eq(cid))
                .where(qOrder.episod.eq(12))
                .fetch();
        Assertions.assertTrue(fetch.size() >= 1);
    }
}