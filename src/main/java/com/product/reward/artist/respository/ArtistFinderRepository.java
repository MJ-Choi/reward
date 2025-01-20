package com.product.reward.artist.respository;

import com.product.reward.entity.QArtist;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ArtistFinderRepository implements ArtistFinder {

    private final JPAQueryFactory factory;

    /**
     * 작가 테이블에 작가 존재 여부 조회
     *
     * @param aid
     * @return
     */
    @Override
    public boolean isExist(Long aid) {
        QArtist qArtist = QArtist.artist;
        Long count = factory.select(Expressions.asNumber(aid))
                .from(qArtist)
                .where(qArtist.aid.eq(aid))
                .limit(1)
                .fetchOne();
        return Objects.equals(aid, count);
    }

    /**
     * 등록된 작가 목록 리스트
     * key: artist
     * value: (Map) { aid:artistName }
     * @return 작가 목록
     */
    public Map<Long, String> getArtistMap() {
        QArtist qArtist = QArtist.artist;
        List<Tuple> fetch = factory.select(
                        qArtist.aid,
                        qArtist.name
                )
                .from(qArtist)
                .fetch();
        return fetch.stream().collect(Collectors.toMap(
                tuple -> tuple.get(0, Long.class),
                tuple -> tuple.get(1, String.class)
        ));
    }
}
