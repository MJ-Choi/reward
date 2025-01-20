package com.product.reward.comic.repository;

import com.product.reward.comic.dto.OrderDto;
import com.product.reward.entity.QOrder;
import com.product.reward.util.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Repository
@AllArgsConstructor
public class OrderRepository {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;

    @Transactional
    public boolean insertOrder(OrderDto orderDto) {
        QOrder qOrder = QOrder.order;
        long execute = factory.insert(qOrder)
                .columns(qOrder.cid,
                        qOrder.mid,
                        qOrder.episod,
                        qOrder.createdDmt
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
}
