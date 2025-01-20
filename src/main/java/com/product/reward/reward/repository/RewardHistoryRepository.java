package com.product.reward.reward.repository;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.config.AppConfiguration;
import com.product.reward.entity.QRewardHistory;
import com.product.reward.reward.dto.RewardDto;
import com.product.reward.reward.dto.RewardHistoryDto;
import com.product.reward.reward.dto.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.SQLInsertClause;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@AllArgsConstructor
public class RewardHistoryRepository {

    private final JPAQueryFactory factory;
    private final DataSource ds;
    private final Configuration configuration;
    private final AppConfiguration config;

    public List<RewardHistoryDto> getRewardHistory(UserType type, Long id) {
        QRewardHistory history = QRewardHistory.rewardHistory;
        return factory.select(Projections.constructor(RewardHistoryDto.class,
                        history.createdDmt,
                        history.rank,
                        history.point
                ))
                .from(history)
                .where(history.type.eq(type.getCode()))
                .where(history.id.eq(id))
                .fetch();
    }

    /**
     * 리워드 지급
     *
     * @param rewardInfo 리워드 정보
     * @param artistMap  작가 정보 (Map) {aid:cid}
     * @param memberMap  소비자 정보 (Map) {cid:List<mid>}
     * @return
     */
    @Transactional
    public boolean payment(RewardDto rewardInfo, Map<Long, Long> artistMap, Map<Long, List<Long>> memberMap) {
        QRewardHistory qRewardHistory = QRewardHistory.rewardHistory;
        int totalRowSize = artistMap.size() + memberMap.values().stream().mapToInt(List::size).sum();

        int batchSize = config.getDbBulkSize();
        boolean result = true;

        try {
            SQLInsertClause sqlInsertClause = new SQLInsertClause(ds.getConnection(), configuration, (RelationalPath<?>) qRewardHistory);
            int count = 0; // 배치 카운트

            // 작가 리워드 등록
            for (Map.Entry<Long, Long> entry : artistMap.entrySet()) { //entry = {aid:cid}
                sqlInsertClause
                        .set(qRewardHistory.rid, rewardInfo.getRid())
                        .set(qRewardHistory.cid, entry.getValue())
                        .set(qRewardHistory.type, UserType.ARTIST.getCode())
                        .set(qRewardHistory.id, entry.getKey())
                        .addBatch();

                // 배치가 batchSize에 도달하면 executeBatch 호출
                if (++count % batchSize == 0) {
                    sqlInsertClause.execute();
                }
            }
            // 소비자 리워드 등록
            for (Map.Entry<Long, List<Long>> entry : memberMap.entrySet()) {
                for (Long mid : entry.getValue()) {
                    sqlInsertClause
                            .set(qRewardHistory.rid, rewardInfo.getRid())
                            .set(qRewardHistory.cid, entry.getKey())
                            .set(qRewardHistory.type, UserType.USER.getCode())
                            .set(qRewardHistory.id, mid)
                            .addBatch();

                    // 배치가 batchSize에 도달하면 executeBatch 호출
                    if (++count % batchSize == 0) {
                        sqlInsertClause.execute();
                    }
                }
            }
            // 마지막 남은 배치 처리
            if (count % batchSize != 0) {
                sqlInsertClause.execute();
            }
        } catch (SQLException e) {
            log.error("failed to register history: {}/{}", totalRowSize, batchSize, e);
            throw new ResponseException(ErrorCode.SQL_ERROR);
        }
        return result;
    }

}