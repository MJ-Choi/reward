package com.product.reward.artist.dto.request;

import com.product.reward.artist.dto.ComicState;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 작품 등록
 */
@Getter
@Setter
@ToString
public class ComicRegisterRequest {

    @NotBlank
    private String comicName;

    private String state = ComicState.RESERVED.getCode();
}
