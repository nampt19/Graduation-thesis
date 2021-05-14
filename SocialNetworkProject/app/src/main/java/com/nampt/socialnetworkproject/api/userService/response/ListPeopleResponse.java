package com.nampt.socialnetworkproject.api.userService.response;

import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.PeopleSearch;

import java.util.List;

public class ListPeopleResponse extends BaseResponse {
    List<PeopleSearch> data;

    public List<PeopleSearch> getData() {
        return data;
    }

    public void setData(List<PeopleSearch> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListPeopleResponse{" +
                "data=" + data +
                '}';
    }
}
