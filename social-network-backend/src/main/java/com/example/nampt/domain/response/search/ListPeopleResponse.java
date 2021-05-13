package com.example.nampt.domain.response.search;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public class ListPeopleResponse extends BaseResponse {
    List<DataPeopleSearch> data;

    public List<DataPeopleSearch> getData() {
        return data;
    }

    public void setData(List<DataPeopleSearch> data) {
        this.data = data;
    }
}
