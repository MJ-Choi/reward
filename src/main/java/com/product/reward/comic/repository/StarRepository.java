package com.product.reward.comic.repository;

import com.product.reward.comic.dto.OrderDto;
import com.product.reward.entity.QStar;
import com.product.reward.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional
@AllArgsConstructor
public class StarRepository {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;

    public boolean insertStar(OrderDto orderDto) {
        QStar qStar = QStar.star;
        long execute = factory.insert(qStar)
                .columns(
                        qStar.cid,
                        qStar.mid,
                        qStar.episod,
                        qStar.createdDtm
                )
                .values(
                        orderDto.getCid(),
                        orderDto.getMid(),
                        orderDto.getEpisod(),
                        dateUtils.now()
                )
                .execute();
        return execute > 0;
    }

    public boolean deleteStar(OrderDto orderDto) {
        QStar qStar = QStar.star;
        long execute = factory.delete(qStar)
                .where(qStar.cid.eq(orderDto.getCid()))
                .where(qStar.mid.eq(orderDto.getMid()))
                .where(qStar.episod.eq(orderDto.getEpisod()))
                .execute();
        return execute > 0;
    }
}
