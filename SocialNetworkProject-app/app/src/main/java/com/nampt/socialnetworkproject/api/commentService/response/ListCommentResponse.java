package com.nampt.socialnetworkproject.api.commentService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.commentService.response.data.DataSingleComment;

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
