package com.nampt.socialnetworkproject.api.postService.response;

import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.postService.response.data.DataSinglePost;

public class SinglePostResponse extends BaseResponse {
    private DataSinglePost data;

    public DataSinglePost getData() {
        return data;
    }

    public void setData(DataSinglePost data) {
        this.data = data;
    }
}
