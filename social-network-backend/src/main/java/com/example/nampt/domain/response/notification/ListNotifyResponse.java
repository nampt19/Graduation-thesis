package com.example.nampt.domain.response.notification;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public class ListNotifyResponse extends BaseResponse {
    List<DataSingleNotify> data;

    public List<DataSingleNotify> getData() {
        return data;
    }

    public void setData(List<DataSingleNotify> data) {
        this.data = data;
    }
}
