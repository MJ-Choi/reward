package com.product.reward.comic.controller;

import com.product.reward.api.ResponseDto;
import com.product.reward.comic.dto.OrderDto;
import com.product.reward.comic.service.ComicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자가 작품을 조회하거나 좋아요를 설정
 */
@Slf4j
@RestController
@RequestMapping("/comic")
@AllArgsConstructor
public class ComicController {
    private final ComicService service;

    @GetMapping(value = "/{comic}/{episod}")
    public ResponseDto<Boolean> getInfo(
            @RequestHeader Long m,
            @PathVariable String comic, @PathVariable int episod) {
        log.info("Input: {}/{}-{}", m, comic, episod);
        OrderDto orderDto = new OrderDto(comic, m, episod);
        return new ResponseDto<>(service.addViewCount(orderDto));
    }

    @PostMapping(value = "/{comic}/{episod}")
    public ResponseDto<Boolean> selectLike(
            @RequestHeader Long m,
            @PathVariable String comic, @PathVariable int episod,
            @RequestParam(value = "star", required = false, defaultValue = "false") boolean star) {
        log.info("Input: {}/{}/{}", m, comic, star);
        OrderDto orderDto = new OrderDto(comic, m, episod);
        if (star) {
            return new ResponseDto<>(service.addLikeCount(orderDto));
        } else {
            return new ResponseDto<>(service.subLikeCount(orderDto));
        }
    }

}
