package com.nampt.socialnetworkproject.api.userService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.PeopleSearch;

public class PeopleResponse extends BaseResponse {
    PeopleSearch data;

    public PeopleSearch getData() {
        return data;
    }

    public void setData(PeopleSearch data) {
        this.data = data;
    }
}
