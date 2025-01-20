package com.product.reward.artist.respository;

import com.product.reward.artist.dto.ComicDto;

import java.util.List;

public interface ComicFinder {

    ComicDto getComicInfo(Long artistId, String comicName);

    List<ComicDto> getComicInfos(Long artistId, long offset, int limit);

    Long getComicCount(Long artistId);

}
