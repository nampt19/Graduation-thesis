package com.example.nampt.domain.response.post;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.post.data.DataSinglePost;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListPostResponse extends BaseResponse {
    private List<DataSinglePost> data;
    private int lastId;
    private boolean isLastPage;

    public List<DataSinglePost> getData() {
        return data;
    }

    public void setData(List<DataSinglePost> data) {
        this.data = data;
    }

    @JsonProperty("last_id")
    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @JsonProperty("is_last_page")
    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
}
