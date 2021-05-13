package com.nampt.socialnetworkproject.api.userService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;

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
