package com.product.reward.artist.service;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.dto.ComicState;
import com.product.reward.artist.dto.request.ComicEditRequest;
import com.product.reward.artist.dto.request.ComicRegisterRequest;
import com.product.reward.artist.respository.ArtistFinder;
import com.product.reward.artist.respository.ComicFinder;
import com.product.reward.artist.respository.ComicRepository;
import com.product.reward.util.ArtistRedisKey;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ArtistService {

    private final ComicRepository repository;
    private final ArtistFinder artistFinder;
    private final ComicFinder comicFinder;

    private final RedisUtils redisUtils;
    private final ArtistRedisKey artistKey;
    private final CollectRedisKey collectKey;

    public ArtistService(ComicRepository repository, ArtistFinder artistFinder, ComicFinder comicFinder) {
        this.repository = repository;
        this.artistFinder = artistFinder;
        this.comicFinder = comicFinder;
        this.redisUtils = null;
        this.artistKey = null;
        this.collectKey = null;
    }

    public ArtistService(ComicRepository repository, ArtistFinder artistFinder, ComicFinder comicFinder,
                         RedisUtils redisUtils, CollectRedisKey collectKey) {
        this.repository = repository;
        this.artistFinder = artistFinder;
        this.comicFinder = comicFinder;
        this.redisUtils = redisUtils;
        this.artistKey = new ArtistRedisKey();
        this.collectKey = collectKey;
    }

    /**
     * 작품 등록
     *
     * @param artistId 작가ID
     * @param request  요청
     * @return
     */
    public ComicDto register(Long artistId, ComicRegisterRequest request) {
        validateAid(artistId);
        validateName(request.getComicName());

        ComicDto requestDto = new ComicDto();
        requestDto.setAid(artistId);
        requestDto.setName(request.getComicName());
        requestDto.setState(ComicState.fromCode(request.getState()));
        ComicDto result = repository.register(requestDto);

        if (redisUtils != null) {
            // 작가의 작품 등록
            String key = artistKey.artistKey(artistId);
            LinkedHashMap<Long, ComicDto> map = redisUtils.getLinkedMap(key, Long.class, ComicDto.class);
            map.put(result.getCid(), result);
            redisUtils.setStr(key, map);
            // 작품매핑정보 등록
            updateComicMap(result.getName(), result.getCid());
        }

        return result;
    }

    /**
     * 작가의 모든 작품 조회
     *
     * @param artistId 작가ID
     * @param pageable page
     * @return
     */
    public Page<ComicDto> getInfos(Long artistId, Pageable pageable) {
        validateAid(artistId);
        List<ComicDto> list = comicFinder.getComicInfos(artistId, pageable.getOffset(), pageable.getPageSize());
        Long totalSize = comicFinder.getComicCount(artistId);
        return new PageImpl<>(list, pageable, totalSize);
    }

    /**
     * 특정 작품 조회
     *
     * @param artistId  작가ID
     * @param comicName 작품명
     * @return
     */
    public ComicDto getInfo(Long artistId, String comicName) {
        validateAid(artistId);
        return comicFinder.getComicInfo(artistId, comicName);
    }

    /**
     * 작품 수정
     *
     * @param artistId 작가ID
     * @param request  요청
     * @return
     */
    public boolean edit(Long artistId, ComicEditRequest request) {
        log.info("aid:{}/request:{}", artistId, request);
        validateAid(artistId);
        if (request.getEdits().isEmpty()) {
            log.error("request is null");
            return false;
        }

        List<ComicDto> list = request.getEdits().stream()
                .map(requestDto -> {
                    ComicDto comicDto = new ComicDto();
                    comicDto.setAid(artistId);
                    comicDto.setCid(requestDto.getCid());
                    if (StringUtils.hasText(requestDto.getComicName())) {
                        comicDto.setName(requestDto.getComicName());
                    }
                    if (StringUtils.hasText(requestDto.getState())) {
                        comicDto.setState(ComicState.fromCode(requestDto.getState()));
                    }
                    return comicDto;
                })
                .toList();

        // 레디스 데이터 변경
        if (redisUtils != null) {
            String key = artistKey.artistKey(artistId);
            LinkedHashMap<Long, ComicDto> map = redisUtils.getLinkedMap(key, Long.class, ComicDto.class);
            list.forEach(req -> {
                ComicDto comicDto = map.get(req.getCid());
                if (StringUtils.hasText(req.getName())) {
                    comicDto.setName(req.getName());
                    updateComicMap(req.getName(), req.getCid());
                }
                if (req.getState() != null) {
                    comicDto.setState(req.getState());
                }
                map.put(req.getCid(), comicDto);
            });
            redisUtils.setStr(key, map);
        }
        // db 데이터 변경
        return repository.updateComics(list);
    }

    private void validateAid(Long aid) {
        if (!artistFinder.isExist(aid)) {
            log.error("aid({}) is not valid.", aid);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
    }

    private void validateName(String comicName) {
        if (!repository.isUniqueName(comicName)) {
            log.error("comicName({}) is not unique.", comicName);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
    }

    private void updateComicMap(String comicName, Long cid) {
        String mapperKey = collectKey.getComicMapKey();
        Map<String, Long> map = redisUtils.getMap(mapperKey, String.class, Long.class);
        map.put(comicName, cid);
        redisUtils.setStr(mapperKey, map);
    }

}