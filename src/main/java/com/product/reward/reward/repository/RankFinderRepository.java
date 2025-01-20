package com.product.reward.reward.repository;

import com.product.reward.entity.QOrder;
import com.product.reward.entity.QStar;
import com.product.reward.reward.dto.CollectBaseDto;
import com.product.reward.util.DateUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class RankFinderRepository implements RankFinder {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;

    /**
     * 마지막 조회일을 기준으로 기간별 조회수, 좋아요수 조회
     *
     * @param endDtm
     * @return
     */
    public List<CollectBaseDto> makeCollect(LocalDateTime endDtm) {
        QOrder qOrder = QOrder.order;
        QStar qStar = QStar.star;
        LocalDate now = endDtm.toLocalDate();
        LocalDateTime startOfDay = now.atStartOfDay(); // yyyyMMdd 00:00:00
        LocalDateTime endOfDay = now.atTime(LocalTime.MAX); // yyyyMMdd 23:59:59
        LocalDateTime weekOfDay = dateUtils.getWeek(startOfDay);
        LocalDateTime startMonthDay = startOfDay.withDayOfMonth(1);
        LocalDateTime startYearDay = startMonthDay.withDayOfYear(1);
        log.debug("startDay: {}, endDay:{}, week: {}, month: {}, year: {}",
                startOfDay, endOfDay, weekOfDay, startMonthDay, startYearDay);
        return factory.select(Projections.constructor(CollectBaseDto.class,
                        qOrder.cid,
                        viewCnt(startOfDay, endOfDay),
                        viewCnt(weekOfDay, endOfDay),
                        viewCnt(startMonthDay, endOfDay),
                        viewCnt(startYearDay, endOfDay),

                        likeCnt(startOfDay, endOfDay),
                        likeCnt(weekOfDay, endOfDay),
                        likeCnt(startMonthDay, endOfDay),
                        likeCnt(startYearDay, endOfDay)
                ))
                .from(qOrder)
                .leftJoin(qStar).on(qOrder.cid.eq(qStar.cid))
                .groupBy(qOrder.cid)
                .fetch();
    }

    /**
     * 마지막 조회일을 기준으로 기간별 좋아요수 조회
     *
     * @param endDtm
     * @return
     */
    public CollectBaseDto makeLikeCollector(LocalDateTime endDtm, Long cid) {
        QStar qStar = QStar.star;
        LocalDate now = endDtm.toLocalDate();
        LocalDateTime startOfDay = now.atStartOfDay(); // yyyyMMdd 00:00:00
        LocalDateTime endOfDay = now.atTime(LocalTime.MAX); // yyyyMMdd 23:59:59
        LocalDateTime weekOfDay = dateUtils.getWeek(startOfDay);
        LocalDateTime startMonthDay = startOfDay.withDayOfMonth(1);
        LocalDateTime startYearDay = startMonthDay.withDayOfYear(1);
        log.debug("startDay: {}, endDay:{}, week: {}, month: {}, year: {}",
                startOfDay, endOfDay, weekOfDay, startMonthDay, startYearDay);
        return factory.select(Projections.constructor(CollectBaseDto.class,
                        qStar.cid,
                        Expressions.asNumber(0),
                        Expressions.asNumber(0),
                        Expressions.asNumber(0),
                        Expressions.asNumber(0),

                        likeCnt(startOfDay, endOfDay),
                        likeCnt(weekOfDay, endOfDay),
                        likeCnt(startMonthDay, endOfDay),
                        likeCnt(startYearDay, endOfDay)
                ))
                .from(qStar)
                .where(qStar.cid.eq(cid))
                .groupBy(qStar.cid)
                .fetchOne();
    }

    /**
     * 특정 기간의 조회수, 좋아요수 조회
     *
     * @param startDtm
     * @param endDtm
     * @return
     */
    public List<Tuple> makeCollect(LocalDateTime startDtm, LocalDateTime endDtm) {
        QOrder qOrder = QOrder.order;
        QStar qStar = QStar.star;
        LocalDateTime startOfDay = startDtm.toLocalDate().atStartOfDay(); // yyyyMMdd 00:00:00
        LocalDateTime endOfDay = endDtm.toLocalDate().atTime(LocalTime.MAX); // yyyyMMdd 23:59:59
        return factory.select(
                        qOrder.cid,
                        viewCnt(startOfDay, endOfDay),
                        likeCnt(startOfDay, endOfDay)
                )
                .from(qOrder)
                .leftJoin(qStar).on(qOrder.cid.eq(qStar.cid))
                .groupBy(qOrder.cid)
                .fetch();
    }

    private NumberExpression<Integer> viewCnt(LocalDateTime startDt, LocalDateTime endDt) {
        QOrder qOrder = QOrder.order;
        return new CaseBuilder()
                .when(qOrder.createdDmt.between(startDt, endDt))
                .then(1)
                .otherwise(0)
                .sum()
                .coalesce(0);
    }

    private NumberExpression<Integer> likeCnt(LocalDateTime startDt, LocalDateTime endDt) {
        QStar qStar = QStar.star;
        return new CaseBuilder()
                .when(qStar.createdDtm.between(startDt, endDt))
                .then(1)
                .otherwise(0)
                .sum()
                .coalesce(0);
    }

    @Override
    public Map<Long, Integer> dailyTopComics(int top) {
        LocalDateTime startDt = dateUtils.now().toLocalDate().atStartOfDay();
        LocalDateTime endDt = dateUtils.now().toLocalDate().atTime(LocalTime.MAX);
        return periodTopComics(startDt, endDt, top);
    }

    @Override
    public Map<Long, Integer> weeklyTopComics(int top) {
        LocalDateTime startDt = dateUtils.getWeek(dateUtils.now());
        LocalDateTime endDt = dateUtils.now().toLocalDate().atTime(LocalTime.MAX);
        return periodTopComics(startDt, endDt, top);
    }

    @Override
    public Map<Long, Integer> monthlyTopComics(int top) {
        LocalDateTime startDt = dateUtils.now().withDayOfMonth(1);
        LocalDateTime endDt = dateUtils.now().toLocalDate().atTime(LocalTime.MAX);
        return periodTopComics(startDt, endDt, top);
    }

    @Override
    public Map<Long, Integer> yearlyTopComics(int top) {
        LocalDateTime startDt = dateUtils.now().withDayOfYear(1);
        LocalDateTime endDt = dateUtils.now().toLocalDate().atTime(LocalTime.MAX);
        return periodTopComics(startDt, endDt, top);
    }

    private Map<Long, Integer> periodTopComics(LocalDateTime startDt, LocalDateTime endDt, int top) {
        log.info("duration: {} - {}", startDt, endDt);
        QOrder qOrder = QOrder.order;
        QStar qStar = QStar.star;
        // 1. 상위 10번째 value 값 조회
        NumberExpression<Integer> value = viewCnt(startDt, endDt)
                .add(likeCnt(startDt, endDt).multiply(2))
                .coalesce(0);
        Integer thresholdValue = factory.select(value)
                .from(qOrder)
                .leftJoin(qStar).on(qOrder.cid.eq(qStar.cid))
                .groupBy(qOrder.cid)
                .orderBy(value.desc())
                .offset(top - 1)
                .limit(1)
                .fetchOne();
        if (thresholdValue == null) {
            thresholdValue = 0;
        }
        log.info("thresholdValue:{}", thresholdValue);

        // 점수가 중복되는 작품을 포함한 list 반환
        return factory
                .select(
                        qOrder.cid.coalesce(qStar.cid),
                        value
                )
                .from(qOrder)
                .leftJoin(qStar).on(qOrder.cid.eq(qStar.cid))
                .groupBy(qOrder.cid)
                .having(value.goe(thresholdValue))
                .orderBy(value.desc())
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
    }

}