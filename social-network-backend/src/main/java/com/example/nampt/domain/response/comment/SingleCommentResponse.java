package com.example.nampt.domain.response.comment;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.comment.data.DataSingleComment;

public class SingleCommentResponse extends BaseResponse {
    private DataSingleComment data;

    public DataSingleComment getData() {
        return data;
    }

    public void setData(DataSingleComment data) {
        this.data = data;
    }
}
