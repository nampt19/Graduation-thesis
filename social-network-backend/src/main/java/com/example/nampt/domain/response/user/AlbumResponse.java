package com.example.nampt.domain.response.user;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public class AlbumResponse extends BaseResponse {
    List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
