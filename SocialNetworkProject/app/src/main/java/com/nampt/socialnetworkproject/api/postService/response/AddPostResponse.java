package com.nampt.socialnetworkproject.api.postService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;

public class AddPostResponse extends BaseResponse {
    private int post_id;

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    @Override
    public String toString() {
        return "AddPostResponse{" +
                "post_id=" + post_id +
                " code=" + getCode() +
                " message=" + getMessage() +
                '}';
    }
}
