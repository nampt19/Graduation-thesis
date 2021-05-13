package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public class ListMessageResponse extends BaseResponse {
    List<DataSingleMessage> data;

    public List<DataSingleMessage> getData() {
        return data;
    }

    public void setData(List<DataSingleMessage> data) {
        this.data = data;
    }
}
