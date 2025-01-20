package com.product.reward.reward.controller;

import com.product.reward.api.ResponseDto;
import com.product.reward.reward.dto.RewardDto;
import com.product.reward.reward.dto.RewardHistoryDto;
import com.product.reward.reward.dto.request.RewardRequest;
import com.product.reward.reward.service.RewardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reward")
@AllArgsConstructor
public class RewardController {

    private final RewardService service;

    /**
     * 작품 등록
     *
     * @param request 리워드 요청 정보
     * @return 성공 여부 (true: 성공 / false: 실패)
     */
    @PostMapping(value = "")
    public ResponseDto<RewardDto> register(@RequestBody RewardRequest request) {
        log.info("Input: {}", request);

        return new ResponseDto<>(service.register(request));
    }

    /**
     * 관리자가 리워드 지급
     *
     * @param rid
     * @return
     */
    @PostMapping(value = "/{rid}")
    public ResponseDto<Boolean> payReward(@PathVariable long rid) {
        log.info("Input: {}", rid);

        return new ResponseDto<>(service.payment(rid));
    }

    /**
     * 지급받은 리워드 조회
     *
     * @param t        유저타입(a: 작가, m: 소비자)
     * @param id       작가 혹은 소비자 id
     * @param pageable 페이징
     * @return
     */
    @GetMapping(value = "/history")
    public ResponseDto<Page<RewardHistoryDto>> history(
            @RequestParam(value = "t", required = false) String t,
            @RequestParam(value = "id", required = false) Long id,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        log.info("Input: {}/{}/{}", t, id, pageable);
        if (StringUtils.hasText(t) && id != null) {
            return new ResponseDto<>(service.history(t, id, pageable));
        } else {
            return new ResponseDto<>(service.history(pageable));
        }
    }
}
