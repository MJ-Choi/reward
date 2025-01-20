package com.product.reward.artist.respository;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.artist.dto.ComicDto;
import com.product.reward.util.ArtistRedisKey;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ComicFinderRedis implements ComicFinder {

    private final ArtistRedisKey artistKey;
    private final CollectRedisKey collectKey;
    private final RedisUtils redisUtils;
    private Map<String, Long> comicMapper;

    public ComicFinderRedis(CollectRedisKey collectKey, RedisUtils redisUtils) {
        this.artistKey = new ArtistRedisKey();
        this.collectKey = collectKey;
        this.redisUtils = redisUtils;
        this.comicMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
    }

    @Override
    public ComicDto getComicInfo(Long artistId, String comicName) {
        Long cid = cid(comicName);
        LinkedHashMap<Long, ComicDto> map = getComicInfo(artistId);
        if (map.isEmpty() || !map.containsKey(cid)) {
            log.error("artist:{}-{}-{} not valid comic", artistId, cid, comicName);
            throw new ResponseException(ErrorCode.NO_DATA);
        }
        return map.get(cid);
    }

    @Override
    public List<ComicDto> getComicInfos(Long artistId, long offset, int limit) {
        LinkedHashMap<Long, ComicDto> map = getComicInfo(artistId);
        List<Long> comics = new ArrayList<>(map.keySet());
        if (offset >= comics.size()) {
            log.error("offset({}) is out of range.(maxSize: {})", offset, comics.size());
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
        if (limit > comics.size()) {
            limit = comics.size();
        }
        log.info("aid:{}/offset:{}/limit:{}", artistId, offset, limit);
        return comics.subList((int) offset, limit)
                .stream()
                .map(map::get).toList();
    }

    @Override
    public Long getComicCount(Long artistId) {
        LinkedHashMap<Long, ComicDto> map = getComicInfo(artistId);
        return (long) map.size();
    }

    private LinkedHashMap<Long, ComicDto> getComicInfo(Long artistId) {
        return redisUtils.getLinkedMap(artistKey.artistKey(artistId), Long.class, ComicDto.class);
    }

    private Long cid(String comicName) {
        if (!this.comicMapper.containsKey(comicName)) {
            this.comicMapper = redisUtils.getMap(collectKey.getComicMapKey(), String.class, Long.class);
        }
        // todo. remove log
        log.info("comicMapper: {}", this.comicMapper);
        return this.comicMapper.get(comicName);
    }
}