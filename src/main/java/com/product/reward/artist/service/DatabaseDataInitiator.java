package com.product.reward.artist.service;

import com.product.reward.artist.dto.ComicState;
import com.product.reward.entity.QArtist;
import com.product.reward.entity.QComic;
import com.product.reward.entity.QMember;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class DatabaseDataInitiator {

    private final JPAQueryFactory factory;

    private final String[] artists = {"a", "b", "c", "d"};

    public void init() {
        initArtist();
        initUser();
        initComic();
    }

    public void initArtist() {
        QArtist qArtist = QArtist.artist;
        if (isCreated("artist-a", qArtist.name, qArtist)) {
            log.info("[artist] table already has datas.");
            return;
        }
        Arrays.stream(artists).toList().forEach(name ->
                factory.insert(qArtist)
                        .columns(qArtist.name)
                        .values("artist-".concat(name))
                        .execute()
        );
    }

    public void initUser() {
        QMember qMember = QMember.member;
        String[] users = new String[10];
        for (int i = 0; i < users.length; i++) {
            users[i] = "user-".concat(String.valueOf(i + 1));
        }

        if (isCreated("user-1", qMember.userName, qMember)) {
            log.info("[members] table already has datas.");
            return;
        }

        Arrays.stream(users).toList().forEach(name ->
                factory.insert(qMember)
                        .columns(qMember.userName)
                        .values(name)
                        .execute()
        );

    }

    public void initComic() {
        QComic qComic = QComic.comic;
        QArtist qArtist = QArtist.artist;

        if (isCreated("arng", qComic.name, qComic)) {
            log.info("[comics] table already has datas.");
            return;
        }

        Arrays.stream(artists).toList().forEach(name -> {
            Long aid = factory.select(qArtist.aid)
                    .from(qArtist)
                    .where(qArtist.name.eq("artist-".concat(name)))
                    .fetchOne();
            log.debug("insert aid({}) comics", aid);

            factory.insert(qComic)
                    .columns(
                            qComic.name,
                            qComic.aid,
                            qComic.state
                    )
                    .values(
                            name.concat("rng"),
                            aid,
                            ComicState.OPEN.getCode()
                    )
                    .execute();
            factory.insert(qComic)
                    .columns(
                            qComic.name,
                            qComic.aid,
                            qComic.state
                    )
                    .values(
                            name.concat("rsv"),
                            aid,
                            ComicState.RESERVED.getCode()
                    )
                    .execute();
        });
    }

    private boolean isCreated(String name, StringExpression expr, EntityPath table) {
        String value = factory.select(expr)
                .from(table)
                .where(expr.eq(name))
                .fetchOne();
        if (value != null && value.equals(name)) {
            return true;
        }
        return false;
    }

}