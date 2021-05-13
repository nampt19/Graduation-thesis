package com.example.nampt.domain.response.comment;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.comment.data.DataSingleComment;

import java.util.List;

public class ListCommentResponse  extends BaseResponse {
    private List<DataSingleComment> data;

    public List<DataSingleComment> getData() {
        return data;
    }

    public void setData(List<DataSingleComment> data) {
        this.data = data;
    }
}
