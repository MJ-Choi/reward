package com.product.reward.util;

/**
 * 초기정보는 RedisDataInitiator 에서 적재
 */
public class ArtistRedisKey {

    /**
     * 등록된 작가 목록 리스트
     * key: artist
     * value: (Map) {artistId: artistName}
     * @return
     */
    public String artistListKey() {
        return "artist";
    }

    /**
     * 작품 dto 호출 키
     * key: artist:{artistId}
     * value: (Map) {comicName: {comicDto} }
     * @param aid 작가ID
     * @return
     */
    public String artistKey(long aid) {
        return "artist:".concat(String.valueOf(aid));
    }
}
