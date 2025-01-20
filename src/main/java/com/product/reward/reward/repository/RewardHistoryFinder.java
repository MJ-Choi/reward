package com.product.reward.reward.repository;

import com.product.reward.entity.QComic;
import com.product.reward.entity.QReward;
import com.product.reward.entity.QRewardHistory;
import com.product.reward.reward.dto.RewardHistoryDto;
import com.product.reward.reward.dto.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class RewardHistoryFinder {

    private final JPAQueryFactory factory;

    public List<RewardHistoryDto> getRewardHistory(UserType type, Long id, long offset, int limit) {
        QRewardHistory history = QRewardHistory.rewardHistory;
        QComic qComic = QComic.comic;
        QReward qReward = QReward.reward;
        return factory.select(Projections.constructor(RewardHistoryDto.class,
                        qComic.name,
                        qReward.rewardType,
                        history.createdDmt,
                        history.rank,
                        history.point
                ))
                .from(history)
                .leftJoin(qComic).on(qComic.cid.eq(history.cid))
                .leftJoin(qReward).on(qReward.rid.eq(history.rid))
                .where(history.type.eq(type.getCode()))
                .where(history.id.eq(id))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public List<RewardHistoryDto> getRewardHistory(long offset, int limit) {
        QRewardHistory history = QRewardHistory.rewardHistory;
        QComic qComic = QComic.comic;
        QReward qReward = QReward.reward;
        return factory.select(Projections.constructor(RewardHistoryDto.class,
                        qComic.name,
                        qReward.rewardType,
                        history.createdDmt,
                        history.rank,
                        history.point
                ))
                .from(history)
                .leftJoin(qComic).on(qComic.cid.eq(history.cid))
                .leftJoin(qReward).on(qReward.rid.eq(history.rid))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public Long getHistoryCount() {
        QRewardHistory history = QRewardHistory.rewardHistory;
        return factory.select(history.count())
                .from(history)
                .fetchOne();
    }

    public Long getHistoryCount(UserType type, Long id) {
        QRewardHistory history = QRewardHistory.rewardHistory;
        return factory.select(history.count())
                .from(history)
                .where(history.type.eq(type.getCode()))
                .where(history.id.eq(id))
                .fetchOne();
    }
}
