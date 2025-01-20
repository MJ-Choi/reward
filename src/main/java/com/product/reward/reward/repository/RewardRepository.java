package com.product.reward.reward.repository;

import com.product.reward.entity.QArtist;
import com.product.reward.entity.QReward;
import com.product.reward.reward.dto.RewardDto;
import com.product.reward.util.DateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class RewardRepository {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;

    public List<RewardDto> getInfos(long offset, int limit) {
        QReward qReward = QReward.reward;
        return factory.select(Projections.constructor(RewardDto.class,
                        qReward.rid,
                        qReward.rewardType,
                        qReward.state,
                        qReward.rewardDtm,
                        qReward.collectDtm
                ))
                .from(qReward)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public RewardDto getInfo(Long rid) {
        QReward qReward = QReward.reward;
        return factory.select(Projections.constructor(RewardDto.class,
                        qReward.rid,
                        qReward.rewardType,
                        qReward.state,
                        qReward.rewardDtm,
                        qReward.collectDtm
                ))
                .from(qReward)
                .where(qReward.rid.eq(rid))
                .fetchOne();
    }

    /**
     * 관리자가 생성한 리워드 등록
     *
     * @param rewardDto
     * @return
     */
    public boolean register(RewardDto rewardDto) {
        QReward qReward = QReward.reward;
        long execute = factory.insert(qReward)
                .columns(
                        qReward.rewardType,
                        qReward.state,
                        qReward.rewardDtm,
                        qReward.collectDtm
                )
                .values(
                        rewardDto.getType().getCode(),
                        rewardDto.getState().getCode(),
                        rewardDto.getRewardDtm(),
                        rewardDto.getCollectDtm()
                )
                .execute();
        return execute > 0;
    }

    /**
     * 리워드 요청 수정
     *
     * @param rewardDto
     * @return
     */
    public boolean edit(RewardDto rewardDto) {
        QReward qReward = QReward.reward;
        JPAUpdateClause updateClause = factory.update(qReward);
        updateClause.where(qReward.rid.eq(rewardDto.getRid()));
        if (rewardDto.getType() != null) {
            updateClause.set(qReward.rewardType, rewardDto.getType().getCode());
        }
        if (rewardDto.getState() != null) {
            updateClause.set(qReward.state, rewardDto.getState().getCode());
        }
        if (rewardDto.getRewardDtm() != null) {
            updateClause.set(qReward.rewardDtm, rewardDto.getRewardDtm());
        }
        if (rewardDto.getCollectDtm() != null) {
            updateClause.set(qReward.collectDtm, rewardDto.getCollectDtm());
        }
        long execute = updateClause.execute();
        return execute > 0;
    }

}