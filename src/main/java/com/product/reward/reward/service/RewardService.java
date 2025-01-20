package com.product.reward.reward.service;

import com.product.reward.reward.dto.RewardDto;
import com.product.reward.reward.dto.RewardHistoryDto;
import com.product.reward.reward.dto.request.RewardRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RewardService {

    public RewardDto register(RewardRequest request) {
        return null;
    }

    public boolean payment(Long rid) {
        return true;
    }

    public Page<RewardHistoryDto> history(Pageable pageable) {
        return null;
    }

    public Page<RewardHistoryDto> history(String t, Long id, Pageable pageable) {
        return null;
    }

}
