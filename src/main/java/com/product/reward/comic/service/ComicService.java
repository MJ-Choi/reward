package com.product.reward.comic.service;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.comic.dto.OrderDto;
import com.product.reward.comic.repository.RedisCountCollector;
import com.product.reward.comic.repository.OrderRepository;
import com.product.reward.comic.repository.StarRepository;
import com.product.reward.reward.dto.CollectBaseDto;
import com.product.reward.reward.repository.RankFinderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ComicService {

    private final OrderRepository orderRepository;
    private final StarRepository starRepository;
    private final RedisCountCollector redisCounter;

    private final Clock clock;
    private final RankFinderRepository rankRepository;

    public boolean addViewCount(OrderDto orderDto) {
        Long cid = validateName(orderDto.getComicName());
        orderDto.setCid(cid);
        redisCounter.addViewCount(orderDto.getComicName());
        return orderRepository.insertOrder(orderDto);
    }

    public boolean addLikeCount(OrderDto orderDto) {
        Long cid = validateName(orderDto.getComicName());
        orderDto.setCid(cid);
        redisCounter.addLikeCount(orderDto.getComicName());
        return starRepository.insertStar(orderDto);
    }

    public boolean subLikeCount(OrderDto orderDto) {
        Long cid = validateName(orderDto.getComicName());
        orderDto.setCid(cid);
        CollectBaseDto baseDto = rankRepository.makeLikeCollector(LocalDateTime.now(clock), cid);
        redisCounter.subLikeCount(orderDto.getComicName(), baseDto);
        return starRepository.deleteStar(orderDto);
    }

    private Long validateName(String name) {
        Long cid = redisCounter.cid(name);
        if (cid == null || cid == 0L) {
            log.error("comicName: {}/cid: {}", name, cid);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
        return cid;
    }

}