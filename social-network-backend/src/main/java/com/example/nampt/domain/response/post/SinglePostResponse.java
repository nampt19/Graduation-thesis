package com.example.nampt.domain.response.post;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.post.data.DataSinglePost;

import java.util.List;

public class SinglePostResponse extends BaseResponse {
    private DataSinglePost data;

    public DataSinglePost getData() {
        return data;
    }

    public void setData(DataSinglePost data) {
        this.data = data;
    }
}
