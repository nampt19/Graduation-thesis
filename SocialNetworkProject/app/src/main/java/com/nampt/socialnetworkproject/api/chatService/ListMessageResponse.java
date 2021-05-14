package com.nampt.socialnetworkproject.api.chatService;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.MessageRow;

import java.util.List;

public class ListMessageResponse extends BaseResponse {
    List<MessageRow> data;

    public List<MessageRow> getData() {
        return data;
    }

    public void setData(List<MessageRow> data) {
        this.data = data;
    }
}
