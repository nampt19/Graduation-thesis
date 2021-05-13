package com.example.nampt.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeResponse extends BaseResponse {
    private int totalLike;

    @JsonProperty("total_like")
    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }
}
