package com.product.reward.reward.service;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.reward.dto.*;
import com.product.reward.reward.dto.request.RewardEditRequest;
import com.product.reward.reward.dto.request.RewardRequest;
import com.product.reward.reward.repository.RewardHistoryFinder;
import com.product.reward.reward.repository.RewardHistoryRepository;
import com.product.reward.reward.repository.RewardRepository;
import com.product.reward.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardHistoryRepository historyRepository;

    private final RewardHistoryFinder historyFinder;

    private final DateUtils dateUtils;

    /**
     * 리워드 요청 등록
     * @param request
     * @return
     */
    public Boolean register(RewardRequest request) {
        RewardDto rewardDto = new RewardDto();
        rewardDto.setType(RewardType.fromCode(request.getRewardType()));
        rewardDto.setState(RewardState.CREATED);
        rewardDto.setRewardDtm(dateUtils.strToDt(request.getRewardDate()));
        rewardDto.setCollectDtm(dateUtils.strToDt(request.getRequestDate()));
        return rewardRepository.register(rewardDto);
    }

    public boolean edit(RewardEditRequest request) {
        RewardDto info = rewardRepository.getInfo(request.getRid());
        if (info == null) {
            log.error("It's not register: {}", request);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
        validateState(info.getState());

        RewardDto rewardDto = new RewardDto();
        if (StringUtils.hasText(request.getRewardDate())) {
            rewardDto.setRewardDtm(dateUtils.strToDt(request.getRewardDate()));
        }
        if (StringUtils.hasText(request.getRequestDate())) {
            rewardDto.setCollectDtm(dateUtils.strToDt(request.getRequestDate()));
        }
        if (StringUtils.hasText(request.getRewardType())) {
            rewardDto.setType(RewardType.fromCode(request.getRewardType()));
        }
        if (StringUtils.hasText(request.getState())) {
            rewardDto.setState(RewardState.fromCode(request.getState()));
        }
        return rewardRepository.edit(rewardDto);
    }

    public boolean payment(Long rid) {
//        RewardDto info = rewardRepository.getInfo(rid);
//        historyRepository.payment(info, );
        return true;
    }

    public Page<RewardHistoryDto> history(Pageable pageable) {
        List<RewardHistoryDto> rewardHistory = historyFinder.getRewardHistory(pageable.getOffset(), pageable.getPageSize());
        Long totalSize = historyFinder.getHistoryCount();
        return new PageImpl<>(rewardHistory, pageable, totalSize);
    }

    public Page<RewardHistoryDto> history(String t, Long id, Pageable pageable) {
        List<RewardHistoryDto> rewardHistory = historyFinder.getRewardHistory(UserType.fromCode(t), id, pageable.getOffset(), pageable.getPageSize());
        Long totalSize = historyFinder.getHistoryCount();
        return new PageImpl<>(rewardHistory, pageable, totalSize);
    }

    private void validateState(RewardState state) {
        if (state != RewardState.CREATED) {
            log.error("cannot change the state: {}", state);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
    }

}
