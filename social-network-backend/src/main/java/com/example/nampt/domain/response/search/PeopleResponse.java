package com.example.nampt.domain.response.search;

import com.example.nampt.domain.response.BaseResponse;

public class PeopleResponse extends BaseResponse {
    DataPeopleSearch data;

    public DataPeopleSearch getData() {
        return data;
    }

    public void setData(DataPeopleSearch data) {
        this.data = data;
    }
}
