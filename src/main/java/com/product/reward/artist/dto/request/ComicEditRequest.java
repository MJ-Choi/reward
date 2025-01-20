package com.product.reward.artist.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 작품 수정
 */
@Getter
@Setter
@ToString
public class ComicEditRequest {

    @NotEmpty
    private List<EditRequest> edits;

    @Getter
    @Setter
    @ToString
    public static class EditRequest{

        @Min(1)
        private long cid;

        private String comicName;

        private String state;
    }

}