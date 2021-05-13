package com.nampt.socialnetworkproject.api.postService.response;

import com.google.gson.annotations.SerializedName;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.postService.response.data.DataSinglePost;

import java.util.List;

public class ListPostResponse extends BaseResponse {
    private List<DataSinglePost> data;

    @SerializedName("last_id")
    private int lastId;

    @SerializedName("is_last_page")
    private boolean isLastPage;

    public List<DataSinglePost> getData() {
        return data;
    }

    public void setData(List<DataSinglePost> data) {
        this.data = data;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    @Override
    public String toString() {
        return "ListPostResponse{" +
                "data=" + data +
                '}';
    }
}
