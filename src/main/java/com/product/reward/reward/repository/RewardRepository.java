package com.product.reward.reward.repository;

import com.product.reward.entity.QReward;
import com.product.reward.reward.dto.RewardDto;
import com.product.reward.util.DateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class RewardRepository {

    private final JPAQueryFactory factory;
    private final DateUtils dateUtils;
    private final Configuration configuration;
    private final DataSource ds;

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
        try {
            SQLInsertClause insertClause = new SQLInsertClause(ds.getConnection(), configuration, (RelationalPath<?>) qReward);
            long execute = insertClause
                    .set(qReward.rewardType, rewardDto.getType().getCode())
                    .set(qReward.state, rewardDto.getState().getCode())
                    .set(qReward.rewardDtm, rewardDto.getRewardDtm())
                    .set(qReward.collectDtm, rewardDto.getCollectDtm())
                    .execute();
            return execute > 0;
        } catch (SQLException e) {
            log.error("failed to register: ", e);
        }
        return false;
    }

    /**
     * 리워드 요청 수정
     *
     * @param rewardDto
     * @return
     */
    public boolean edit(RewardDto rewardDto) {
        QReward qReward = QReward.reward;
        try {
            SQLUpdateClause updateClause = new SQLUpdateClause(ds.getConnection(), configuration, (RelationalPath<?>) qReward);
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
        } catch (SQLException e) {
            log.error("failed to register: ", e);
        }
        return false;
    }
}
