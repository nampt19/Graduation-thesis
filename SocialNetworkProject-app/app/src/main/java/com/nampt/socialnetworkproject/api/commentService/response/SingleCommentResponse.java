package com.nampt.socialnetworkproject.api.commentService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.commentService.response.data.DataSingleComment;

public class SingleCommentResponse extends BaseResponse {
    private DataSingleComment data;

    public DataSingleComment getData() {
        return data;
    }

    public void setData(DataSingleComment data) {
        this.data = data;
    }
}
