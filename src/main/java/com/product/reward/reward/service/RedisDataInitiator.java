package com.product.reward.reward.service;

import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.respository.ArtistFinderRepository;
import com.product.reward.artist.respository.ComicFinderRepository;
import com.product.reward.util.ArtistRedisKey;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DB 정보를 redis에 적재
 */
@Slf4j
@Service
public class RedisDataInitiator {
    private final ComicFinderRepository comicFinder;
    private final ArtistFinderRepository artistFinder;
    private final RedisUtils redisUtils;
    private final ArtistRedisKey artistKey;
    private final CollectRedisKey collectKey;

    Map<Long, String> artistMap = null;

    public RedisDataInitiator(ArtistFinderRepository artistFinder, ComicFinderRepository comicFinder,
                              Clock clock, RedisUtils redisUtils) {
        this.artistFinder = artistFinder;
        this.comicFinder = comicFinder;
        this.redisUtils = redisUtils;
        this.artistKey = new ArtistRedisKey();
        this.collectKey = new CollectRedisKey(new DateUtils(clock));
        getArtistMap();
        initLoad();
    }

    private void initLoad() {
        loadArtist();
        loadComics();
    }

    private void getArtistMap() {
        this.artistMap = artistFinder.getArtistMap();
    }

    /**
     * key: artist
     * value: (Map) {artistId: artistName}
     */
    private void loadArtist() {
        redisUtils.setStr(artistKey.artistListKey(), this.artistMap);
    }

    /**
     * key: artist:{artistId}
     * value: (Map) { cid: {comicDto} }
     * <p>
     * key: comics
     * value: (Map) { comicName: {cid} }
     * <p>
     * key: *-collect:{PERIOD}
     * value: (Map) { comicName: {sumCount} }
     */
    private void loadComics() {
        Map<String, Long> comicKeyMap = new HashMap<>();
        Map<String, Integer> dViewMap = new HashMap<>();
        Map<String, Integer> wViewMap = new HashMap<>();
        Map<String, Integer> mViewMap = new HashMap<>();
        Map<String, Integer> yViewMap = new HashMap<>();
        Map<String, Integer> dLikeMap = new HashMap<>();
        Map<String, Integer> wLikeMap = new HashMap<>();
        Map<String, Integer> mLikeMap = new HashMap<>();
        Map<String, Integer> yLikeMap = new HashMap<>();

        for (Long aid : this.artistMap.keySet()) {
            Map<Long, ComicDto> comicMap = new LinkedHashMap<>();
            List<ComicDto> list = comicFinder.getComicInfos(aid);
            list.forEach(dto -> {
                comicMap.put(dto.getCid(), dto);
                comicKeyMap.put(dto.getName(), dto.getCid());

                dViewMap.put(dto.getName(), dto.getDailyViewCnt());
                wViewMap.put(dto.getName(), dto.getWeeklyViewCnt());
                mViewMap.put(dto.getName(), dto.getMonthlyViewCnt());
                yViewMap.put(dto.getName(), dto.getYearlyViewCnt());

                dLikeMap.put(dto.getName(), dto.getDailyLikeCnt());
                wLikeMap.put(dto.getName(), dto.getWeeklyLikeCnt());
                mLikeMap.put(dto.getName(), dto.getMonthlyLikeCnt());
                yLikeMap.put(dto.getName(), dto.getYearlyLikeCnt());
            });
            redisUtils.setStr(artistKey.artistKey(aid), comicMap);
        }
        redisUtils.setStr(collectKey.getComicMapKey(), comicKeyMap);

        redisUtils.setStr(collectKey.getDayCollectKey(), dViewMap);
        redisUtils.setStr(collectKey.getWeekCollectKey(), wViewMap);
        redisUtils.setStr(collectKey.getYearCollectKey(), mViewMap);
        redisUtils.setStr(collectKey.getYearCollectKey(), yViewMap);

        redisUtils.setStr(collectKey.getDayLikeKey(), dLikeMap);
        redisUtils.setStr(collectKey.getWeekLikeKey(), wLikeMap);
        redisUtils.setStr(collectKey.getMonthLikeKey(), mLikeMap);
        redisUtils.setStr(collectKey.getYearLikeKey(), yLikeMap);
    }
}
