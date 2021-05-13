package com.nampt.socialnetworkproject.api.notifyService;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.Notification;

import java.util.List;

public class ListNotifyResponse extends BaseResponse {
    List<Notification> data;

    public List<Notification> getData() {
        return data;
    }

    public void setData(List<Notification> data) {
        this.data = data;
    }
}
