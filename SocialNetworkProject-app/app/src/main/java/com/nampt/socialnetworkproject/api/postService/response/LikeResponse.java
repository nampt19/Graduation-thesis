package com.nampt.socialnetworkproject.api.postService.response;

import com.google.gson.annotations.SerializedName;
import com.nampt.socialnetworkproject.api.BaseResponse;

public class LikeResponse extends BaseResponse {

    @SerializedName("total_like")
    private int totalLike;

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }
}
