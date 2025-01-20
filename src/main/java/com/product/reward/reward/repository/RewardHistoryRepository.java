package com.product.reward.reward.repository;

import com.product.reward.config.AppConfiguration;
import com.product.reward.entity.QRewardHistory;
import com.product.reward.entity.RewardHistory;
import com.product.reward.reward.dto.RewardComicDto;
import com.product.reward.reward.dto.RewardDto;
import com.product.reward.reward.dto.UserType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional
@AllArgsConstructor
public class RewardHistoryRepository {

    private final AppConfiguration config;
    private final EntityManager em;

    /**
     * 리워드 지급
     *
     * @param rewardInfo 리워드 정보
     * @param memberMap  소비자 정보 (Map) {cid:List<mid>}
     * @return
     */
    public boolean payment(RewardDto rewardInfo, Map<Long, List<Long>> memberMap) {
        QRewardHistory qRewardHistory = QRewardHistory.rewardHistory;
        int totalRowSize = rewardInfo.getTop10Comics().stream().map(RewardComicDto::getAid).collect(Collectors.toSet()).size()
                + memberMap.values().stream().mapToInt(List::size).sum();
        int batchSize = config.getDbBulkSize();

        int count = 0; // 배치 카운트
        for (int i = 0; i < totalRowSize; i++) {
            Set<Long> aids = new HashSet<>();
            for (RewardComicDto comic : rewardInfo.getTop10Comics()) {
                // 작가 리워드 등록
                if (aids.contains(comic.getAid())) {
                    continue;
                }
                aids.add(comic.getAid());
                em.persist(new RewardHistory(
                        rewardInfo.getRid(), comic.getCid(),
                        UserType.ARTIST.getCode(), comic.getAid(),
                        comic.getRank(), comic.getScore()
                ));
                // 배치가 batchSize에 도달하면 executeBatch 호출
                if (++count % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                // 소비자 리워드 등록
                for (Map.Entry<Long, List<Long>> entry : memberMap.entrySet()) {
                    for (Long mid : entry.getValue()) {
                        em.persist(new RewardHistory(
                                rewardInfo.getRid(), comic.getCid(),
                                UserType.ARTIST.getCode(), mid,
                                comic.getRank(), comic.getScore()
                        ));
                        // 배치가 batchSize에 도달하면 executeBatch 호출
                        if (++count % batchSize == 0) {
                            em.flush();
                            em.clear();
                        }
                    }
                }
            }
            // 마지막 남은 배치 처리
            if (count % batchSize != 0) {
                em.flush();
                em.clear();
            }
        }
        return true;
    }
}