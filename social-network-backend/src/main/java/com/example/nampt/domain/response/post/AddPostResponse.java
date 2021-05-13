package com.example.nampt.domain.response.post;

import com.example.nampt.domain.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddPostResponse extends BaseResponse {
    private int postId;

    @JsonProperty("post_id")
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
