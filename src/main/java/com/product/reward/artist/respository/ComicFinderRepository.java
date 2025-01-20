package com.product.reward.artist.respository;

import com.product.reward.artist.dto.ComicDto;
import com.product.reward.entity.QComic;
import com.product.reward.entity.QOrder;
import com.product.reward.entity.QStar;
import com.product.reward.util.DateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class ComicFinderRepository implements ComicFinder {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;

    @Override
    public ComicDto getComicInfo(Long artistId, String comicName) {
        QComic qComic = QComic.comic;
        return selectCase(artistId)
                .where(qComic.name.eq(comicName))
                .fetchOne();
    }

    /**
     * 작품 정보 조회
     *
     * @param artistId 작가ID
     * @param offset   offset
     * @param limit    limit
     * @return
     */
    @Override
    public List<ComicDto> getComicInfos(Long artistId, long offset, int limit) {
        return selectCase(artistId)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    // 레디스에 데이터를 적재하기 위해 전체 작품 정보 조회
    public List<ComicDto> getComicInfos(Long artistId) {
        return selectCase(artistId).fetch();
    }

    @Override
    public Long getComicCount(Long artistId) {
        QComic qComic = QComic.comic;
        return factory.select(qComic.cid.count())
                .from(qComic)
                .where(qComic.aid.eq(artistId))
                .fetchOne();
    }

    private JPAQuery<ComicDto> selectCase(Long artistId) {
        QComic qComic = QComic.comic;
        QOrder qOrder = QOrder.order;
        QStar qStar = QStar.star;

        LocalDate now = dateUtils.now().toLocalDate();
        LocalDateTime startOfDay = now.atStartOfDay(); // yyyyMMdd 00:00:00
        LocalDateTime endOfDay = now.atTime(LocalTime.MAX); // yyyyMMdd 23:59:59
        LocalDateTime weekOfDay = dateUtils.getWeek(startOfDay);
        LocalDateTime startMonthDay = startOfDay.withDayOfMonth(1);
        LocalDateTime startYearDay = startOfDay.withDayOfYear(1);

        return factory.select(Projections.constructor(ComicDto.class,
                        qComic.cid,
                        Expressions.asNumber(artistId),
                        qComic.name,
                        qComic.state,

                        viewCnt(startOfDay, endOfDay),
                        viewCnt(weekOfDay, endOfDay),
                        viewCnt(startMonthDay, endOfDay),
                        viewCnt(startYearDay, endOfDay),

                        likeCnt(startOfDay, endOfDay),
                        likeCnt(weekOfDay, endOfDay),
                        likeCnt(startMonthDay, endOfDay),
                        likeCnt(startYearDay, endOfDay)
                ))
                .from(qComic)
                .leftJoin(qStar).on(qStar.cid.eq(qComic.cid))
                .leftJoin(qOrder).on(qOrder.cid.eq(qComic.cid))
                .where(qComic.aid.eq(artistId))
                .groupBy(qComic.cid);
    }

    private NumberExpression<Integer> viewCnt(LocalDateTime startDt, LocalDateTime endDt) {
        QOrder qOrder = QOrder.order;
        return new CaseBuilder()
                .when(qOrder.createdDmt.between(startDt, endDt))
                .then(1)
                .otherwise(0)
                .sum();
    }

    private NumberExpression<Integer> likeCnt(LocalDateTime startDt, LocalDateTime endDt) {
        QStar qStar = QStar.star;
        return new CaseBuilder()
                .when(qStar.createdDtm.between(startDt, endDt))
                .then(1)
                .otherwise(0)
                .sum();
    }

}