package com.product.reward.artist.controller;

import com.product.reward.api.ResponseDto;
import com.product.reward.artist.dto.ComicDto;
import com.product.reward.artist.dto.request.ComicEditRequest;
import com.product.reward.artist.dto.request.ComicRegisterRequest;
import com.product.reward.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 작가가 작품을 관리
 */
@Slf4j
@RestController
@RequestMapping("/artist")
@AllArgsConstructor
public class ArtistController {

    private final ArtistService service;

    /**
     * 작품 등록
     * @param artistId 작가ID
     * @param request 작품정보
     * @return 성공 여부 (true: 성공 / false: 실패)
     */
    @PostMapping(value = "")
    public ResponseDto<ComicDto> register(
            @RequestHeader("A") Long artistId,
            @RequestBody ComicRegisterRequest request) {
        log.info("Input: {} : {}", artistId, request);

        return new ResponseDto<>(service.register(artistId, request));
    }

    /**
     * 작품 전체 조회
     * @param artistId 작가ID
     * @param pageable page
     * @return 작품정보 목록
     */
    @GetMapping(value = "/comics")
    public ResponseDto<Page<ComicDto>> getInfos(
            @RequestHeader("A") Long artistId,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        log.info("Input: {} : {}", artistId, pageable);
        return new ResponseDto<>(service.getInfos(artistId, pageable));
    }

    /**
     * 작품 한 건 조회
     * @param artistId 작가ID
     * @param comicName 작품명
     * @return 작품정보
     */
    @GetMapping(value = "/{comic}")
    public ResponseDto<ComicDto> getInfo(
            @RequestHeader("A") Long artistId,
            @PathVariable String comicName) {
        log.info("Input: {} : {}", artistId, comicName);
        return new ResponseDto<>(service.getInfo(artistId, comicName));
    }

    /**
     * 작품 수정
     * @param artistId 작가ID
     * @param request 수정내용
     * @return 성공 여부 (true: 성공 / false: 실패)
     */
    @PostMapping(value = "/comics/edit")
    public ResponseDto<Boolean> edit(
            @RequestHeader("A") Long artistId,
            @RequestBody ComicEditRequest request) {
        log.info("Input: {}", request);
        return new ResponseDto<>(service.edit(artistId, request));
    }

}